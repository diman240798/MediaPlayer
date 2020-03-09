package com.nanicky.devteam.main.playback

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.nanicky.devteam.common.urlEncoded
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.db.recently.RecentlyPlayedRepository
import com.nanicky.devteam.main.db.AppRoomDatabase
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongsRepository
import com.nanicky.devteam.main.lyrics.Lyrics
import com.nanicky.devteam.main.lyrics.LyricsFetcher
import com.nanicky.devteam.main.lyrics.LyricsRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaybackViewModel(
    application: Application,
    mediaSessionConnection: MediaSessionConnection,
    private val preferences: SharedPreferences,
    private val currentQueueSongsRepository: CurrentQueueSongsRepository,
    private val browseTree: BrowseTree
) :
    AndroidViewModel(application) {


    var lyrics = MutableLiveData<Lyrics?>()
    private val playedRepository: RecentlyPlayedRepository
    private val lyricsRepository: LyricsRepository
    private val _mediaItems = MutableLiveData<MutableList<MediaItemData>>()
    private val _currentItem = MutableLiveData<MediaItemData?>()
    private val _playbackState =
        MutableLiveData<PlaybackStateCompat>().apply { value = EMPTY_PLAYBACK_STATE }
    private val _shuffleMode = MutableLiveData<Int>().apply {
        value = preferences.getInt(
            Constants.LAST_SHUFFLE_MODE,
            PlaybackStateCompat.SHUFFLE_MODE_NONE
        )
    }
    private val _repeatMode = MutableLiveData<Int>().apply {
        value = preferences.getInt(
            Constants.LAST_REPEAT_MODE,
            PlaybackStateCompat.REPEAT_MODE_NONE
        )
    }
    private val _mediaPosition =
        MutableLiveData<Long>().apply { value = preferences.getLong(Constants.LAST_POSITION, 0) }
    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())
    private var playMediaAfterLoad: String? = null

    val mediaItems: LiveData<MutableList<MediaItemData>> = _mediaItems
    val currentItem: LiveData<MediaItemData?> = _currentItem
    val playbackState: LiveData<PlaybackStateCompat> = _playbackState
    val mediaPosition: LiveData<Long> = _mediaPosition
    val shuffleMode: LiveData<Int> = _shuffleMode
    val repeatMode: LiveData<Int> = _repeatMode


    init {
        val database = AppRoomDatabase.getDatabase(application)
        playedRepository = RecentlyPlayedRepository(database.recentDao())
        lyricsRepository = LyricsRepository(database.lyricsDao())
    }

    fun playPause() {
        if (mediaSessionConnection.playbackState.value?.isPlayingOrBuffering == true) {
            mediaSessionConnection.transportControls.pause()
        } else {
            playMediaId(currentItem.value?.id)
        }
    }


    fun playFromSearch(id: String) {
        val parentId = lastParendId
        val list = mediaItems.value

        if (lastParendId == Constants.SONGS_SEARCH && list != null && !list.isEmpty()) {
            playMediaId(getItemFrmPlayId(id, list)?.id)
        } else {
            lastParendId = Constants.SONGS_SEARCH
            playMediaAfterLoad = id
            mediaSessionConnection.unsubscribe(parentId, subscriptionCallback)
            mediaSessionConnection.subscribe(Constants.SONGS_SEARCH, subscriptionCallback)
        }
    }

    fun playMediaId(mediaId: String?) {
        if (mediaId == null) return
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            if (mediaSessionConnection.playbackState.value?.isPauseEnabled == true) {
                mediaSessionConnection.transportControls.play()
            }
        } else {
            val extras = Bundle()
            extras.putString("uri", lastParendId)
            transportControls.playFromMediaId(mediaId, extras)
            transportControls.setRepeatMode(repeatMode.value!!)
            transportControls.setShuffleMode(shuffleMode.value!!)
        }
    }

    fun playAlbum(album: Album, playId: String = Constants.PLAY_FIRST) =
        playMedia(album.id.urlEncoded, playId)

    fun playFavourites(playId: String = Constants.PLAY_FIRST) =
        playMedia(Constants.FAVOURITES_ROOT, playId)

    fun playFolder(folderPath: String, playId: String = Constants.PLAY_FIRST) =
        playMedia(folderPath, playId)

    fun playPlaylist(playlistId: String, playId: String = Constants.PLAY_FIRST) =
        playMedia(playlistId, playId)

    fun playGenre(playlistId: String, playId: String = Constants.PLAY_FIRST) =
        playMedia(playlistId, playId)

    private fun playMedia(listSourcePath: String, playStartId: String) {
        val parentId = lastParendId
        val list = mediaItems.value
        if (parentId == listSourcePath && list != null && !list.isEmpty()) {
            playMediaId(getItemFrmPlayId(playStartId, list)?.id)
        } else {
            playMediaAfterLoad = playStartId
            mediaSessionConnection.unsubscribe(parentId, subscriptionCallback)
            mediaSessionConnection.subscribe(listSourcePath, subscriptionCallback)
        }
    }

    fun playAll(playId: String = Constants.PLAY_RANDOM) {
        val parentId = lastParendId
        val list = mediaItems.value
        if (parentId == Constants.SONGS_ROOT && list != null) {
            playMediaId(getItemFrmPlayId(playId, list)?.id)
        } else {
            playMediaAfterLoad = playId
            mediaSessionConnection.unsubscribe(parentId, subscriptionCallback)
            mediaSessionConnection.subscribe(Constants.SONGS_ROOT, subscriptionCallback)
        }
    }

    fun seek(time: Long) {
        val transportControls = mediaSessionConnection.transportControls
        transportControls.seekTo(time)
        preferences.edit().putLong(Constants.LAST_POSITION, time).apply()
    }

    fun setShuffleMode() {
        val newValue = when (shuffleMode.value) {
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> PlaybackStateCompat.SHUFFLE_MODE_NONE
            else -> PlaybackStateCompat.SHUFFLE_MODE_ALL
        }
        mediaSessionConnection.transportControls.setShuffleMode(newValue)
    }

    fun setRepeatMode() {
        val newValue = when (repeatMode.value) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> PlaybackStateCompat.REPEAT_MODE_ONE
            PlaybackStateCompat.REPEAT_MODE_ONE -> PlaybackStateCompat.REPEAT_MODE_ALL
            PlaybackStateCompat.REPEAT_MODE_ALL -> PlaybackStateCompat.REPEAT_MODE_NONE
            else -> PlaybackStateCompat.REPEAT_MODE_NONE
        }
        mediaSessionConnection.transportControls.setRepeatMode(newValue)
    }

    fun skipToNext() {
        if (mediaSessionConnection.playbackState.value?.started == true) {
            mediaSessionConnection.transportControls.skipToNext()
        } else {
            _mediaItems.value?.let {
                val i = it.indexOf(currentItem.value)
                // Only skip to the next item if the current item is not the last item in the list
                if (i != (it.size - 1)) _currentItem.postValue(it[(i + 1)])
            }
        }
    }

    fun skipToPrevious() {
        if (mediaSessionConnection.playbackState.value?.started == true) {
            mediaSessionConnection.transportControls.skipToPrevious()
        } else {
            _mediaItems.value?.let {
                val i = it.indexOf(currentItem.value)
                // Only skip to the previous item if the current item is not first item in the list
                if (i > 1) _currentItem.postValue(it[(i - 1)])
            }
        }
    }

    fun addToQueue(song: Song) {
        val metaSong = MediaMetadataCompat.Builder().apply {
            id = song.id
            title = song.title
            album = song.album.name
            artist = song.album.artist
            albumId = song.album.id.toLong()
            albumArtUri = song.artPath
            mediaUri = song.path
            duration = song.duration
        }.build()
        addToQueue(metaSong)
    }


    fun addToQueue(metadataCompat: MediaMetadataCompat) {
        val items = mediaItems.value ?: return
        val index = items.indexOf(currentItem.value)
        if (index != -1) {
            currentQueueSongsRepository.add(index + 1, metadataCompat)
            items.add(index + 1, MediaItemData(metadataCompat, false, false))
            mediaSessionConnection.addToQueue(metadataCompat.description, index + 1)
        } else {
            currentQueueSongsRepository.add(metadataCompat)
            items.add(MediaItemData(metadataCompat, false, false))
            mediaSessionConnection.addToQueue(metadataCompat.description)
        }
        _mediaItems.value = items
    }

    fun removeFromQueue(mediaDescription: MediaDescriptionCompat) {
        mediaSessionConnection.removeFromQueue(mediaDescription)
    }

    fun addToQueue() {
        currentItem.value?.description?.let { mediaSessionConnection.addToQueue(it) }
    }

    fun removeFromQueue() {
        val items = mediaItems.value ?: return
        currentItem.value?.description?.let {
            mediaSessionConnection.removeFromQueue(it)
            items.remove(currentItem.value!!)
            currentQueueSongsRepository.remove(it)
        }
    }


    // When the session's [PlaybackStateCompat] changes, the [mediaItems] needs to be updated
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val state = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(state, metadata)?.toMutableList())
        }
    }

    // When the session's [MediaMetadataCompat] changes, the [mediaItems] needs to be updated
    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null) {
            _mediaItems.postValue(updateState(playbackState, metadata)?.toMutableList())
        }
    }

    private val shuffleObserver = Observer<Int>(_shuffleMode::postValue)

    private val repeatObserver = Observer<Int>(_repeatMode::postValue)


    private fun updateState(state: PlaybackStateCompat, metadata: MediaMetadataCompat):
            List<MediaItemData>? {
        val items =
            (_mediaItems.value?.map { it.copy(isPlaying = it.id == metadata.id && state.isPlayingOrBuffering) }
                ?: emptyList())

        val currentItem = if (items.isEmpty()) {
            // Only update media item if playback has started
            if (state.started) {
                MediaItemData(metadata, state.isPlaying, state.isBuffering)
            } else {
                null
            }
        } else {
            // Only update media item once we have duration available
            if (metadata.duration != 0L && items.isNotEmpty()) {
                val matchingItem = items.firstOrNull { it.id == metadata.id }
                matchingItem?.apply {
                    isPlaying = state.isPlaying
                    isBuffering = state.isBuffering
                    duration = metadata.duration
                }
            } else null
        }

        // Update synchronously so addToRecentlyPlayed can pick up a valid currentItem
        if (currentItem != null) _currentItem.value = currentItem
        _playbackState.postValue(state)
        if (state.started) updatePlaybackPosition()
        return items
    }

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val items = children.map {
                MediaItemData(
                    it,
                    isItemPlaying(it.mediaId!!),
                    isItemBuffering(it.mediaId!!)
                )
            }
            val current = if (!playMediaAfterLoad.isNullOrBlank()) {
                getItemFrmPlayId(playMediaAfterLoad!!, items)
            } else {
                (items.firstOrNull { it.isPlaying }
                    ?: items.firstOrNull { it.id == preferences.getString(Constants.LAST_ID, null) }
                    ?: items.firstOrNull())
            }

            viewModelScope.launch {
                // Let's get the duration of the current playing song if it's the same as our filter above
                val currentValue = currentItem.value
                if (current != null) {
                    if (currentValue != null && (current.id == currentValue.id)) {
                        current.duration = currentValue.duration
                    } else {
                        val value = withContext(Dispatchers.IO) {
                            playedRepository.fetchFirst()
                        }
                        current.duration = value?.duration ?: 0
                    }
                }
                _mediaItems.postValue(items.toMutableList())
                _currentItem.postValue(current)
                // Re-post the media position so views like SeekBars can pickup the new view
                _mediaPosition.postValue(mediaPosition.value)

                if (!playMediaAfterLoad.isNullOrBlank() && current != null) {
                    playMediaId(current.id)
                    playMediaAfterLoad = null
                }

                currentQueueSongsRepository.insert(lastParendId)
            }
        }
    }

    private fun getItemFrmPlayId(playId: String, items: List<MediaItemData>): MediaItemData? {
        return when (playId) {
            Constants.PLAY_FIRST -> items.firstOrNull()
            Constants.PLAY_RANDOM -> items.random()
            else -> items.firstOrNull { it.id == playId }
        }
    }

    private fun isItemPlaying(mediaId: String): Boolean {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
        return isActive && isPlaying
    }

    private fun isItemBuffering(mediaId: String): Boolean {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isBuffering = mediaSessionConnection.playbackState.value?.isBuffering ?: false
        return isActive && isBuffering
    }

    /**
     *  Because there's a complex dance between this [AndroidViewModel] and the [MediaSessionConnection]
     *  (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using [Transformations]
     *  doesn't quite work.
     *
     *  Specifically there's three things that are watched that will cause the single piece of [LiveData]
     *  exposed from this class to be updated
     *
     *  [subscriptionCallback] (defined above) is called if/when the children of this ViewModel's [mediaId] changes
     *
     *  [MediaSessionConnection.playbackState] changes state based on the playback state of
     *  the player, which can change the [MediaItemData.isPlaying]s in the list.
     *
     *  [MediaSessionConnection.nowPlaying] changes based on the item that's being played,
     *  which can also change [MediaItemData.isPlaying]s in the list.
     */
    private val mediaSessionConnection = mediaSessionConnection.also {
        preferences.edit().putString(Constants.LAST_PARENT_ID, Constants.CURRENT_QUEUE_ROOT)
            .commit()
        it.subscribe(Constants.CURRENT_QUEUE_ROOT, subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        it.repeatMode.observeForever(repeatObserver)
        it.shuffleMode.observeForever(shuffleObserver)
    }


    /**
     * Internal function that recursively calls itself every [POSITION_UPDATE_INTERVAL_MILLIS] ms
     * to check the current playback position and updates the corresponding LiveData object when it
     * has changed.
     */
    private fun updatePlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = _playbackState.value?.currentPlayBackPosition
        if (_mediaPosition.value != currPosition) {
            _mediaPosition.postValue(currPosition)
        }
        if (updatePosition)
            updatePlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    /**
     * Since we use [LiveData.observeForever] above (in [mediaSessionConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [ViewModel]
     * is not longer in use.
     *
     * For more details, see the kdoc on [mediaSessionConnection] above.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MediaSessionConnection.
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        mediaSessionConnection.repeatMode.removeObserver(repeatObserver)
        mediaSessionConnection.shuffleMode.removeObserver(shuffleObserver)

        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaSessionConnection.rootMediaId, subscriptionCallback)

        // Stop updating the position
        updatePosition = false

        handler.removeCallbacksAndMessages(null)
    }

    fun getLyrics(id: String, artist: String, song: String): Job {
        return viewModelScope.launch {
            lyrics.value = withContext(Dispatchers.IO) {
                // todo: move to lyricsRepository
                val lyricsFromBd: Lyrics? = lyricsRepository.getLyrics(id)
                if (lyricsFromBd != null) {
                    return@withContext lyricsFromBd
                } else {
                    val lyrics = LyricsFetcher.fetchLyrics(id, artist, song)
                    lyricsRepository.save(lyrics)
                    lyrics.id = id
                    return@withContext lyrics
                }
            }
        }
    }

    fun addMediaRootToQueue(urlEncoded: String) {
        browseTree[urlEncoded]!!.forEach {
            addToQueue(it)
        }
    }

    private var lastParendId: String
        get() = preferences.getString(
            Constants.LAST_PARENT_ID,
            Constants.SONGS_ROOT
        )!!
        set(value) {
            preferences
                .edit()
                .putString(Constants.LAST_PARENT_ID, value)
                .commit()
        }

}

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
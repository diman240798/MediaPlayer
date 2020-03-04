// // A little modification was made from the original file: https://raw.githubusercontent.com/googlesamples/android-UniversalMusicPlayer/master/common/src/main/java/com/example/android/uamp/media/library/BrowseTree.kt

package com.nanicky.devteam.main.playback.mediasource

import android.content.Context
import android.database.ContentObserver
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.nanicky.devteam.R
import com.nanicky.devteam.common.urlEncoded
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongsRepository
import com.nanicky.devteam.main.db.favourite.FavouriteSongsRepository
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.playback.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


/**
 *
 * Represents a tree of media that's used by [PlaybackService.onLoadChildren].
 *
 * [BrowseTree] maps a media id (see: [MediaMetadataCompat.METADATA_KEY_MEDIA_ID]) to one (or more)
 * [MediaMetadataCompat] objects, which are children of the media id.
 *
 * For example, given the following conceptual tree:
 * root
 *  +-- Albums
 *  |    +-- Album_A
 *  |    |    +-- Song_1
 *  |    |    +-- Song_2
 *  ...
 *  +-- Artists
 *  ...
 *
 * Requesting `browserTree["root"]` would return a list that included "Albums", "Artists", and any other direct
 * children. Taking the media ID of "Albums" ("Albums" in this example), `browseTree["Albums"]` would return a single
 * item list "Album_A", and, finally, `browseTree["Album_A"]` would return "Song_1" and "Song_2". Since those are leaf
 * nodes, requesting `browseTree["Song_1"]` would return null (there aren't any children of it).
 */
class BrowseTree(
    val context: Context,
    val musicSource: MediaStoreSource,
    val playlistRepository: PlaylistRepository,
    val favouriteSongsRepository: FavouriteSongsRepository,
    val currentSongRepository: CurrentQueueSongsRepository,
    val mediaUpdateNotifier: MediaUpdateNotifier
) {

    var currentMediaSource: ConcatenatingMediaSource? = null

    val mediaIdToChildren = ConcurrentHashMap<String, CopyOnWriteArrayList<MediaMetadataCompat>>()

    operator fun get(parentId: String) = mediaIdToChildren[parentId]

    /**
     * Whether to allow clients which are unknown (non-whitelisted) to use search on this
     * [BrowseTree].
     */
    val searchableByUnknownCaller = true



    private val observer: ContentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            loadNew(context)
            mediaUpdateNotifier.update()
        }
    }

    private fun loadNew(context: Context) {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            musicSource.loadNew(context).collect {
                workoutItem(it, context)
            }
        }
    }

    /**
     * In this example, there's a single root note (identified by the constant [Constants.BROWSABLE_ROOT].
     * The root's children are each album included in the [MusicSource],
     * and the childrenn of each album are songs on that album. See [buildAlbum] for details
     * TODO: Expand to allow more browsing types.
     */
    fun load() {

        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            currentSongRepository.load()
            favouriteSongsRepository.load()
            playlistRepository.load()

            buildRoots(context)

            musicSource.load(context).collect {
                workoutItem(it, context)
                sortPlayList()
            }
            context.contentResolver.registerContentObserver(baseSongUri, true, observer)
        }
    }

    private fun sortPlayList() {
        playlistRepository.getPlaylists().forEach {
            val playListSongs: CopyOnWriteArrayList<MediaMetadataCompat> = mediaIdToChildren[it.getUniqueKey()]!!
            if (playListSongs.isEmpty()) return@forEach


            val sortedPlaylistSongs = CopyOnWriteArrayList<MediaMetadataCompat>()

            for (songId in it.songIds) {
                val song = playListSongs.firstOrNull { it.id == songId }
                song?.let { sortedPlaylistSongs.add(it) }
            }
            mediaIdToChildren[it.getUniqueKey()] = sortedPlaylistSongs

        }
    }

    private fun buildRoots(context: Context) {
        val rootList = mediaIdToChildren[Constants.BROWSABLE_ROOT] ?: CopyOnWriteArrayList()
        val songsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.SONGS_ROOT
            title = context.getString(R.string.songs)
            albumArtUri =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_song)
            flag = MediaItem.FLAG_PLAYABLE
        }.build()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.ALBUMS_ROOT
            title = context.getString(R.string.albums)
            albumArtUri =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_album)
        }.build()


        val artistsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.ARTISTS_ROOT
            title = context.getString(R.string.artists)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_microphone)
        }.build()

        val foldersMetadata = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.FOLDERS_ROOT
            title = context.getString(R.string.folders)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_folder)
        }.build()

        val genresMetadata = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.GENRES_ROOT
            title = context.getString(R.string.genres)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_album)
        }.build()

        val favouritesMetadata = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.FAVOURITES_ROOT
            title = context.getString(R.string.favourites)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_heart)
        }.build()

        val playlistMetaData = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.PLAYLISTS_ROOT
            title = context.getString(R.string.playlist)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_playlist)
        }.build()


        rootList += songsMetadata
        rootList += albumsMetadata
        rootList += artistsMetadata
        rootList += foldersMetadata
        rootList += genresMetadata
        rootList += favouritesMetadata
        rootList += playlistMetaData


        mediaIdToChildren[Constants.FAVOURITES_ROOT] = CopyOnWriteArrayList()
        mediaIdToChildren[Constants.PLAYLISTS_ROOT] = buildPlaylistRoot()
        mediaIdToChildren[Constants.BROWSABLE_ROOT] = rootList
    }

    private fun workoutItem(mediaItem: MediaMetadataCompat, context: Context) {
        val albumMediaId = mediaItem.albumId.urlEncoded
        val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(mediaItem)
        albumChildren += mediaItem


        val artistMediaId = mediaItem.artist.urlEncoded
        val artistChildren = mediaIdToChildren[artistMediaId] ?: buildArtistRoot(mediaItem)
        artistChildren += mediaItem

        val songsChildren =
            mediaIdToChildren[Constants.SONGS_ROOT] ?: CopyOnWriteArrayList()
        songsChildren += mediaItem
        mediaIdToChildren[Constants.SONGS_ROOT] = songsChildren

        val file = File(mediaItem.mediaUri.toString())
        val parentFile = file.parentFile
        val foldersChildren = mediaIdToChildren[parentFile.path] ?: buildFoldersRoot(
            mediaItem,
            parentFile.path,
            parentFile.name
        )
        foldersChildren += mediaItem


        val songId = mediaItem.id?.toInt()
        val genre = songId?.let { getGenreForSongBySongId(context, songId) }
        genre?.let { genre ->
            val genresChildren = mediaIdToChildren[genre.getUniqueKey()] ?: buildGenresRoot(mediaItem, genre)
            genresChildren += mediaItem
        }

        mediaItem.id?.also { songId ->
            // check playlists
            playlistRepository.getPlaylists()
                .filter { !it.songIds.isEmpty() }
                .filter { it.songIds.contains(songId)  }
                .forEach { playlist ->
                    val url = playlist.getUniqueKey()
                    val playlistSongs = mediaIdToChildren[url] ?: CopyOnWriteArrayList()
                    val songCount = playlist.songIds.filter { it == songId }.size
                    for (i in 0..songCount) playlistSongs += mediaItem // add song several times if it repeats
                    mediaIdToChildren[url] = playlistSongs
            }

            // check favourites
            val songIsFavourite = favouriteSongsRepository.containsId(songId)
            if (songIsFavourite) {
                val favRoot = mediaIdToChildren[Constants.FAVOURITES_ROOT]!!
                favRoot += mediaItem
                mediaIdToChildren[Constants.FAVOURITES_ROOT] = favRoot
            }
        }
    }

    private fun buildPlaylistRoot(): CopyOnWriteArrayList<MediaMetadataCompat> {
        val playlists = playlistRepository.getPlaylists()


        val metaDataPlaylists = playlists.map { playlist ->

            mediaIdToChildren[playlist.getUniqueKey()] = CopyOnWriteArrayList()

            playlistToMedia(playlist)
        }


        return CopyOnWriteArrayList(metaDataPlaylists)
    }

    private fun playlistToMedia(playlist: Playlist): MediaMetadataCompat
            = MediaMetadataCompat.Builder().apply {
            id = playlist.id.toString()
            title = playlist.name
            songIds = playlist.songIds.joinToString(",")
            imagePath = playlist.imagePath
        }.build()

    private val genresProjection = arrayOf(
        MediaStore.Audio.Genres.NAME,
        MediaStore.Audio.Genres._ID
    )

    fun getGenreForSongBySongId(context: Context, songId: Int): Genre? {
        val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songId)
        var genresCursor = context.contentResolver.query(
            uri, genresProjection, null, null, null
        )
        genresCursor?.use {
            if (!genresCursor.moveToNext()) return null
            val id =
                genresCursor.getString(genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
            val name =
                genresCursor.getString(genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME))
            return Genre(id, name)
        }
        return null
    }

    /**
     * Builds a node, under the root, that represents an album, given
     * a [MediaMetadataCompat] object that's one of the songs on that album,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildAlbumRoot(metadata: MediaMetadataCompat): CopyOnWriteArrayList<MediaMetadataCompat> {
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            albumId = metadata.albumId
            album = metadata.album
            artist = metadata.artist
            albumArt = metadata.albumArt
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this album to the 'Albums' category.
        val rootList = mediaIdToChildren[Constants.ALBUMS_ROOT] ?: CopyOnWriteArrayList()
        rootList += albumMetadata
        mediaIdToChildren[Constants.ALBUMS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return CopyOnWriteArrayList<MediaMetadataCompat>().also {
            mediaIdToChildren[albumMetadata.albumId.urlEncoded] = it
        }
    }

    /**
     * Builds a node, under the root, that represents an artist, given
     * a [MediaMetadataCompat] object that's one of the songs on that artist,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildArtistRoot(metadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            id = metadata.artist.urlEncoded
            title = metadata.artist
            //            albumArtUri = metadata.albumArtUri.toString()
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.ARTISTS_ROOT] ?: CopyOnWriteArrayList()
        rootList += artistMetadata
        mediaIdToChildren[Constants.ARTISTS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return CopyOnWriteArrayList<MediaMetadataCompat>().also {
            mediaIdToChildren[artistMetadata.id!!] = it
        }
    }


    /** // TODO: FIX COMMENTS
     * Builds a node, under the root, that represents a folder, given
     * a [MediaMetadataCompat] object that's one of the songs on that folder,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildFoldersRoot(
        metadata: MediaMetadataCompat,
        path: String,
        name: String
    ): MutableList<MediaMetadataCompat> {
        val folderMetadata = MediaMetadataCompat.Builder().apply {
            id = metadata.id.urlEncoded
            title = name
            mediaUri = path
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.FOLDERS_ROOT] ?: CopyOnWriteArrayList()
        rootList += folderMetadata
        mediaIdToChildren[Constants.FOLDERS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return CopyOnWriteArrayList<MediaMetadataCompat>().also {
            mediaIdToChildren[path] = it
        }
    }

    /** // TODO: FIX COMMENTS
     * Builds a node, under the root, that represents a folder, given
     * a [MediaMetadataCompat] object that's one of the songs on that folder,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildGenresRoot(
        metadata: MediaMetadataCompat,
        genre: Genre
    ): MutableList<MediaMetadataCompat> {
        val genreMetadata = MediaMetadataCompat.Builder().apply {
            id = genre.id
            title = genre.getUniqueKey()
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.GENRES_ROOT] ?: CopyOnWriteArrayList()
        rootList += genreMetadata
        mediaIdToChildren[Constants.GENRES_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return CopyOnWriteArrayList<MediaMetadataCompat>().also {
            mediaIdToChildren[genre.getUniqueKey()] = it
        }
    }

    fun search(query: String, bundle: Bundle): List<MediaMetadataCompat> {
        // First attempt to search with the "focus" that's provided in the bundle
        val focusSearchResult = when (bundle[MediaStore.EXTRA_MEDIA_FOCUS]) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                // For a Genre focused search, only genre is set.
                val genre = bundle[MediaStore.EXTRA_MEDIA_GENRE]
                mediaIdToChildren[Constants.SONGS_ROOT]!!.filter {
                    it.genre == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                // For an Artist focused search, only the artist is set.
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                mediaIdToChildren[Constants.SONGS_ROOT]!!.filter {
                    (it.artist == artist || it.albumArtist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                // For an Album focused search, album and artist are set.
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                val album = bundle[MediaStore.EXTRA_MEDIA_ALBUM]
                mediaIdToChildren[Constants.SONGS_ROOT]!!.filter {
                    (it.artist == artist || it.albumArtist == artist) && it.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                // For a Song (aka Media) focused search, title, album, and artist are set.
                val title = bundle[MediaStore.EXTRA_MEDIA_TITLE]
                val album = bundle[MediaStore.EXTRA_MEDIA_ALBUM]
                val artist = bundle[MediaStore.EXTRA_MEDIA_ARTIST]
                mediaIdToChildren[Constants.SONGS_ROOT]!!.filter {
                    (it.artist == artist || it.albumArtist == artist) && it.album == album
                            && it.title == title
                }
            }
            else -> {
                // There isn't a focus, so no results yet.
                emptyList()
            }
        }

        // Check if we found any results from the focused search
        if (focusSearchResult.isNotEmpty()) return focusSearchResult

        // The query can be null if the user asked to "play music", or something similar.
        // Let's just return them all, shuffled as something to play
        if (query.isBlank()) return listOf()

        // Let's check check the query against a few fields
        return mediaIdToChildren[Constants.SONGS_ROOT]!!.filter {
            it.title?.contains(query) ?: false
                    || it.genre?.contains(query) ?: false
                    || it.artist?.contains(query) ?: false
                    || it.album?.contains(query) ?: false
                    || it.author?.contains(query) ?: false
                    || it.composer?.contains(query) ?: false
                    || it.composer?.contains(query) ?: false
        }
    }

    fun removeFromFavourites(id: String) {
        val favRoot = mediaIdToChildren[Constants.FAVOURITES_ROOT]
        val song = favRoot!!.first { it.id == id }
        favRoot.remove(song)
        mediaUpdateNotifier.update()
    }

    fun addToFavourites(id: String) {
        val favRoot = mediaIdToChildren[Constants.FAVOURITES_ROOT]
        val song = mediaIdToChildren[Constants.SONGS_ROOT]!!.first { it.id == id }
        favRoot!!.add(song)
        mediaUpdateNotifier.update()
    }

    fun addPlaylist(playlist: Playlist) {
        val url = playlist.getUniqueKey()
        val playlistSongs = mediaIdToChildren[url] ?: CopyOnWriteArrayList()
        playlist.songIds.forEach { id ->
            val songsRoot = mediaIdToChildren[Constants.SONGS_ROOT]
            val song = songsRoot!!.firstOrNull { it.id == id }
            song?.also {
                playlistSongs += song
            }

        }
        mediaIdToChildren[url] = playlistSongs
        mediaIdToChildren[Constants.PLAYLISTS_ROOT]!!.add(playlistToMedia(playlist))
        mediaUpdateNotifier.update()
    }

    fun updatePlaylist(playlist: Playlist, name: String) {
        // remove old
        var url = playlist.getUniqueKey()
        mediaIdToChildren.remove(url)
        // add new
        playlist.name = name
        url = playlist.getUniqueKey()
        val playlistSongs = CopyOnWriteArrayList<MediaMetadataCompat>()
        playlist.songIds.forEach { id ->
            val songsRoot = mediaIdToChildren[Constants.SONGS_ROOT]
            val song = songsRoot!!.firstOrNull { it.id == id }
            song?.also {
                playlistSongs += song
            }

        }
        mediaIdToChildren[url] = playlistSongs
        replaceOldPlaylistWithNew(playlist)
    }

    fun addToPlaylist(songId: String, playlist: Playlist) {
        val playlistSongs = mediaIdToChildren[playlist.getUniqueKey()]
        val songsRoot = mediaIdToChildren[Constants.SONGS_ROOT]
        val song = songsRoot!!.first { it.id == songId }
        playlistSongs!!.add(song)
        replaceOldPlaylistWithNew(playlist)
    }

    fun setToPlaylist(playlist: Playlist) {
        val playlistSongs = mediaIdToChildren[playlist.getUniqueKey()]!!
        playlistSongs.clear()

        val songsRoot = mediaIdToChildren[Constants.SONGS_ROOT]
        val songs = songsRoot!!.filter{ playlist.songIds.contains(it.id) }
        playlistSongs.addAll(songs)

        replaceOldPlaylistWithNew(playlist)

    }

    private fun replaceOldPlaylistWithNew(playlist: Playlist) {
        val playlistsInMedia = mediaIdToChildren[Constants.PLAYLISTS_ROOT]!!
        val playlistInMediaList = playlistsInMedia.first { it.id!!.toLong() == playlist.id }
        val index = playlistsInMedia.indexOf(playlistInMediaList)
        playlistsInMedia.removeAt(index)

        val newMediaPlaylist = playlistToMedia(playlist)
        playlistsInMedia.add(index, newMediaPlaylist)
    }

    fun removePlaylist(playlist: Playlist) {
        mediaIdToChildren.remove(playlist.getUniqueKey())
        val index = mediaIdToChildren[Constants.PLAYLISTS_ROOT]?.indexOfFirst { it.id?.toLong() == playlist.id }!!
        mediaIdToChildren[Constants.PLAYLISTS_ROOT]!!.removeAt(index)
        mediaUpdateNotifier.update()
    }

    fun updateVM() = mediaUpdateNotifier.update()
}
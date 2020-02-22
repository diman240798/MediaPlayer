package com.nanicky.devteam.main.playlist

import android.app.Application
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hunter.library.debug.HunterDebug
import com.nanicky.devteam.main.common.event.Event
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.SongsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class PlaylistSongsEditorViewModel(
    application: Application,
    val browseTree: BrowseTree,
    val playlistRepo: PlaylistRepository
) : SongsViewModel(application, browseTree) {
    private val TAG: String = "PlaylistSongsEditorVM"


    private val _playlistValue = MutableLiveData<Event<Boolean>>()
    val playlistValue: LiveData<Event<Boolean>> get() = _playlistValue



    fun reverseSelection(index: Int): Boolean {
        return data.value?.let {
            if (it.size > index) {
                it[index].selected = !it[index].selected
                true
            } else false
        } ?: false

    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                val initiallySelectedItems = browseTree[playlist.getUniqueKey()]!!.map { Song(it) }

                val selectedItems: List<Song> = items.value!!.filter { it.selected }
                // Items that weren't selected initially.
                // A better explanation: Items that doesn't exist in this playlist but are now selected
                val addableItems: List<Song> = selectedItems.minus(initiallySelectedItems)

                // Items that were initially selected but no longer selected
                val removableItems = initiallySelectedItems.minus(selectedItems)


                if (addableItems.isNotEmpty()) {
                    // Add songs to playlist
                    if (!addSongs(addableItems, playlist)) {
                        return@withContext false
                    }
                }

                if (!removableItems.isNullOrEmpty()) {
                    // Remove songs from playlist
                    if (!deleteSongs(removableItems, playlist)) {
                        return@withContext false
                    }

                }
                playlistRepo.insert(playlist)
                return@withContext true
            }
            _playlistValue.value = Event(success)
        }
    }

    @WorkerThread
    @HunterDebug
    private fun deleteSongs(songs: List<Song>,playlist: Playlist): Boolean {
        try {
            songs.map { it.id }.forEach {
                browseTree.removeFromPlaylist(it, playlist)
                playlist.songIds.remove(it)
            }
            return true
        } catch (ex: Exception) {
            Log.d(TAG, ex.message)
            return false
        }

    }

    @WorkerThread
    @HunterDebug
    private fun addSongs(songs: List<Song>, playlist: Playlist): Boolean {
        try {
            songs.map { it.id }.forEach {
                browseTree.addToPlaylist(it, playlist)
                playlist.songIds.add(it)
            }
            return true
        } catch (ex: Exception) {
            Log.d(TAG, ex.message)
            return false
        }
    }
}
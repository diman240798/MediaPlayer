package com.nanicky.devteam.main.playlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanicky.devteam.main.common.event.Event
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.SongsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaylistSongsEditorViewModel(
    application: Application,
    val browseTree: BrowseTree,
    val playlistRepo: PlaylistRepository
) : SongsViewModel(application, browseTree) {
    private val TAG: String = "PlaylistSongsEditorVM"


    lateinit var playlist: Playlist

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

    fun updatePlaylist() {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                val selectedItemsIds: List<String> = items.value!!.filter { it.selected }.map { it.id }

                playlist.songIds.clear()
                playlist.songIds.addAll(selectedItemsIds)

                browseTree.setToPlaylist(playlist)

                playlistRepo.insert(playlist)
                browseTree.updateVM()
                return@withContext true
            }
            _playlistValue.value = Event(success)
        }
    }

    override fun deliverResult(items: List<Song>) {
        items.forEach {
            if (playlist.songIds.contains(it.id)) it.selected = true
        }
        super.deliverResult(items)
    }
}
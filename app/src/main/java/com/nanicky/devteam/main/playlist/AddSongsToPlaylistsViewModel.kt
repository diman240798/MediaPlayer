package com.nanicky.devteam.main.playlist

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.event.Event
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddSongsToPlaylistsViewModel(application: Application, val playlistRepo: PlaylistRepository, val browseTree: BrowseTree) : PlaylistFragmentViewModel(application, browseTree) {
    private val insertionData = MutableLiveData<Event<InsertionResult>>()
    val mediatorItems = MediatorLiveData<Any>()

    override fun init(sourceConst: String?) {
        super.init(sourceConst)

        mediatorItems.addSource(this.items) { mediatorItems.value = it }
        mediatorItems.addSource(insertionData) { mediatorItems.value = it }
    }

    fun addToPlaylist(songId: String?, mediaRoot: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val selectedPlaylists = data.value?.filter { it.selected }

                if (selectedPlaylists == null || selectedPlaylists.isEmpty()) {
                    insertionData.postValue(Event(InsertionResult()))
                    return@withContext
                }

             try {
                 selectedPlaylists.forEach { playlist ->
                     if (songId != null) {
                         playlist.songIds.add(songId)
                         browseTree.addToPlaylist(songId, playlist)
                         playlistRepo.insert(playlist)
                     } else if (mediaRoot != null) {
                         val songs = browseTree.mediaIdToChildren[mediaRoot]!!
                         songs.map { it.id }.forEach {
                             it?.let {
                                 playlist.songIds.add(it)
                                 browseTree.addToPlaylist(it, playlist)
                             }
                         }
                         playlistRepo.insert(playlist)
                     }

                 }
                 insertionData.postValue(Event(InsertionResult(R.string.success, true)))
             } catch (ex: Exception) {
                 insertionData.postValue(Event(InsertionResult(R.string.sth_went_wrong, false)))
             }
            }
        }
    }

}

internal data class InsertionResult(@StringRes val message: Int? = null, val success: Boolean? = null)
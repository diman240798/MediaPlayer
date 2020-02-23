package com.nanicky.devteam.main.playlist

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hunter.library.debug.HunterDebug
import com.nanicky.devteam.R
import com.nanicky.devteam.common.App
import com.nanicky.devteam.main.common.utils.ImageUtils
import com.nanicky.devteam.main.common.utils.UriFileUtils
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception


class WritePlaylistViewModel(
    application: Application,
    val playlistRepo: PlaylistRepository,
    val browseTree: BrowseTree
) :
    AndroidViewModel(application) {

    private val _data = MutableLiveData<WriteResult>()
    internal val data: LiveData<WriteResult> get() = _data

    fun createPlaylist(playlistName: String, tempThumbUri: Uri?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var id = -1L
                try {
                    var newPlaylist = Playlist(playlistName)
                    id = playlistRepo.insert(newPlaylist)
                    newPlaylist = Playlist(id, playlistName)

                    writeImageFile(newPlaylist, tempThumbUri)
                    browseTree.addPlaylist(newPlaylist)
                    playlistRepo.insert(newPlaylist)
                    _data.postValue(WriteResult(true))

                } catch (ex: Exception) {
                    if (id != -1L) playlistRepo.remove(id)
                    _data.postValue(WriteResult(false, R.string.something_went_wrong))
                    return@withContext
                }
            }
        }

    }

    fun editPlaylist(
        name: String,
        playlist: Playlist,
        tempThumbUri: Uri?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    writeImageFile(playlist, tempThumbUri)
                    browseTree.updatePlaylist(playlist, name)
                    playlistRepo.insert(playlist)
                    browseTree.updateVM()
                    _data.postValue(WriteResult(true))

                } catch (ex: Exception) {
                    _data.postValue(WriteResult(false, R.string.sth_went_wrong))
                }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    playlistRepo.remove(playlist)
                    browseTree.removePlaylist(playlist)
                    _data.postValue(WriteResult(true))

                } catch (ex: Exception) {
                    _data.postValue(WriteResult(false, R.string.sth_went_wrong))
                }
            }
        }
    }

    @HunterDebug
    @WorkerThread
    private fun writeImageFile(
        playlist: Playlist,
        tempThumbUri: Uri? = null
    ) {
        val app = getApplication<App>()

        playlist.imagePath?.also {
            val file = File(it)
            if (file.exists()) file.delete()
        }

        if (playlist.imagePath == null) playlist.imagePath = ImageUtils.getImagePathForModel(playlist, app)

        if (tempThumbUri == null) return

        val path = UriFileUtils.getPathFromUri(app, tempThumbUri)
        if (path != null) {
            ImageUtils.resizeImageIfNeeded(path, 300.0, 300.0, 80, playlist.imagePath!!)
        }
        return
    }

}

internal data class WriteResult(val success: Boolean, @StringRes val message: Int? = null)
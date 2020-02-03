package com.nanicky.devteam.main.songs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.playback.MediaItemData

class SongsMenuBottomSheetDialogFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val song: LiveData<Song> get() = _song
    private val _song = MutableLiveData<Song>()

    fun setSong(song: Song) {
        _song.value = song
    }

    fun setSong(mediaItemData: MediaItemData) { // TODO: FixMe: Send Real Song, not this stub
        _song.value = Song(mediaItemData.id, mediaItemData.title, "", Album("0", "", mediaItemData.subtitle), "", 0, "", true, true, 0)
    }
}
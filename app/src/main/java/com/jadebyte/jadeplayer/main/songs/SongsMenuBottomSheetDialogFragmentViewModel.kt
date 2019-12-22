package com.jadebyte.jadeplayer.main.songs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.playback.MediaItemData

class SongsMenuBottomSheetDialogFragmentViewModel : ViewModel() {
    val song get() = _song
    private val _song = MutableLiveData<Song>()

    fun setSong(song: Song) {
        _song.value = song
    }

    fun setSong(mediaItemData: MediaItemData) { // TODO: FixMe: Send Real Song, not this stub
        _song.value = Song(mediaItemData.id.toLong(), mediaItemData.title, "", Album(0, "", mediaItemData.subtitle, ""), "", 0, "", "", true, true, 0)
    }
}
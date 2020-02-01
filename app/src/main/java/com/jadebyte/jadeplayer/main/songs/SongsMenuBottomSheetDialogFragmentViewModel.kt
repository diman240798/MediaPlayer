package com.jadebyte.jadeplayer.main.songs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.db.AppRoomDatabase
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongsRepository
import com.jadebyte.jadeplayer.main.playback.MediaItemData

class SongsMenuBottomSheetDialogFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val song: LiveData<Song> get() = _song
    private val _song = MutableLiveData<Song>()

    fun setSong(song: Song) {
        _song.value = song
    }

    fun setSong(mediaItemData: MediaItemData) { // TODO: FixMe: Send Real Song, not this stub
        _song.value = Song(mediaItemData.id, mediaItemData.title, "", Album("0", "", mediaItemData.subtitle), "", 0, "", "", true, true, 0)
    }
}
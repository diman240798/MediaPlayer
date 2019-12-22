package com.jadebyte.jadeplayer.main.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jadebyte.jadeplayer.main.songs.Song

class WebFragmentViewModel : ViewModel() {
    val searchString get() = _searhString
    private val _searhString = MutableLiveData<String>()

    fun setSearchString(song: Song) {
        _searhString.value = Regex("\\s").replace(song.album.artist, "+")
    }
}

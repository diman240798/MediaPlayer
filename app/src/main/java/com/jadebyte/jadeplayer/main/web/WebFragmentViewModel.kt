package com.jadebyte.jadeplayer.main.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jadebyte.jadeplayer.main.songs.Song

class WebFragmentViewModel : ViewModel() {
    val searchString get() = _searhString
    private val _searhString = MutableLiveData<String>()

    val baseUrl get() = _baseUrl
    private val _baseUrl = MutableLiveData<String>()

    val url get() = _url
    private val _url = MutableLiveData<String>()

    fun setSearchString(song: Song) {
        _searhString.value = song.album.artist.replace("\\s".toRegex(), "+")
    }

    fun setBaseUrl(song: Song) {
        baseUrl.value = song.album.artist.replace("\\s".toRegex(), "+")
    }

    fun setUrl(song: Song) {
        url.value = song.album.artist.replace("\\s".toRegex(), "+")
    }
}

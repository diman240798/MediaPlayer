package com.nanicky.devteam.main.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nanicky.devteam.main.songs.Song

class WebFragmentViewModel : ViewModel() {
    lateinit var urls: MutableList<String>
    val searchString get() = _searhString
    private val _searhString = MutableLiveData<String>()

    val url get() = _url
    private val _url = MutableLiveData<String>()

    fun setSearchString(song: Song) {
        _searhString.value = song.album.artist.replace("\\s".toRegex(), "+")
    }


    fun setUrl(song: Song) {
        url.value = song.album.artist.replace("\\s".toRegex(), "+")
    }

    fun setUrl(url: String) {
        _url.value = url
    }
}

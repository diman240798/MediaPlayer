package com.nanicky.devteam.main.search

import android.app.Application
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.event.Event
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.playback.*
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class SearchViewModel(application: Application, val browseTree: BrowseTree) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<Song>>()
    val songsResults: LiveData<List<Song>> get() = _songs

    private val _albums = MutableLiveData<List<Album>>()
    val albumsResults: LiveData<List<Album>> get() = _albums

    private val _artists = MutableLiveData<List<Artist>>()
    val artistsResults: LiveData<List<Artist>> get() = _artists

    private val _genres = MutableLiveData<List<Genre>>()
    val genresResults: LiveData<List<Genre>> get() = _genres

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlistResults: LiveData<List<Playlist>> get() = _playlists

    private val _resultSize = MutableLiveData<Int>()
    val resultSize: LiveData<Int> get() = _resultSize

    val repository = SearchRepository(application, browseTree)

    fun query(query: String, ascend: Boolean) {
        viewModelScope.launch {
            val songs = async { repository.querySongs(query, ascend) }
            val albums = async { repository.queryAlbums(query, ascend) }
            val artists = async { repository.queryArtists(query, ascend) }
            val genres = async { repository.queryGenres(query, ascend) }
            val playlists = async {
                repository.queryPlaylists(query, ascend)
            }

            browseTree.mediaIdToChildren[Constants.SONGS_SEARCH] = CopyOnWriteArrayList(songs.await())
            _songs.value = browseTree.mediaIdToChildren[Constants.SONGS_SEARCH]!!.map { Song(it) }

            _albums.value = albums.await()
            _artists.value = artists.await()
            _genres.value = genres.await()
            _playlists.value = playlists.await()
            val totalSize = (_songs.value?.size ?: 0) + (_albums.value?.size ?: 0) + (_artists.value?.size
                ?: 0) + (_genres.value?.size ?: 0) + (_playlists.value?.size ?: 0)
            _resultSize.postValue(totalSize)
        }
    }


    private val _searchNavigation =
        MutableLiveData<Event<SearchNavigation>>()
    val searchNavigation: LiveData<Event<SearchNavigation>> get() = _searchNavigation

    fun navigateFrmSearchFragment(navigation: SearchNavigation) = _searchNavigation.postValue(Event(navigation))

}
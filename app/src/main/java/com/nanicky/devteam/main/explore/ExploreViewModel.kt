package com.nanicky.devteam.main.explore

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.albums.AlbumsViewModel
import com.nanicky.devteam.main.db.AppRoomDatabase
import com.nanicky.devteam.main.db.recently.RecentlyPlayed
import com.nanicky.devteam.main.db.recently.RecentlyPlayedRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExploreViewModel(application: Application, browseTree: BrowseTree) :
    AlbumsViewModel(application, browseTree) {
    val inital: MutableLiveData<List<RecentlyPlayed>> = MutableLiveData()
    private val recentlyPlayedRepository: RecentlyPlayedRepository
    val recentlyPlayed: LiveData<List<RecentlyPlayed>>

    init {
        val recentDao = AppRoomDatabase.getDatabase(application).recentDao()
        recentlyPlayedRepository =
            RecentlyPlayedRepository(
                recentDao
            )
        recentlyPlayed = recentlyPlayedRepository.recentlyPlayed

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                inital.postValue(recentlyPlayedRepository.fetchAll())
            }
        }
    }

    override fun deliverResult(items: List<Album>) {
        super.deliverResult(if (items.size > 5) items.subList(0, 5) else items) // TODO: RANDOM?
    }
}

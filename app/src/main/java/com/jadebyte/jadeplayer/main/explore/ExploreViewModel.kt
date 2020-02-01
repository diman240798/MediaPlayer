// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.albums.AlbumsViewModel
import com.jadebyte.jadeplayer.main.db.AppRoomDatabase
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayed
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayedRepository
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Wilberforce on 17/04/2019 at 04:01.
 */
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
        super.deliverResult(items.subList(0, 5)) // TODO: RANDOM?
    }
}

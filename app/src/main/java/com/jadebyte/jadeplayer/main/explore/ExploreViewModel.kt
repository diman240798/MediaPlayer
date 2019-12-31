// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.app.Application
import androidx.lifecycle.LiveData
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.albums.AlbumsViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree

/**
 * Created by Wilberforce on 17/04/2019 at 04:01.
 */
class ExploreViewModel(application: Application, browseTree: BrowseTree) : AlbumsViewModel(application, browseTree) {
    private val recentlyPlayedRepository: RecentlyPlayedRepository
    val recentlyPlayed: LiveData<List<RecentlyPlayed>>

    override var sortOrder: String? = "RANDOM() LIMIT 5"

    init {
        val recentDao = AppRoomDatabase.getDatabase(application).recentDao()
        recentlyPlayedRepository = RecentlyPlayedRepository(recentDao)
        recentlyPlayed = recentlyPlayedRepository.recentlyPlayed
    }

    override fun deliverResult(items: List<Album>) {
        super.deliverResult(items.subList(0, 5)) // TODO: RANDOM?
    }
}

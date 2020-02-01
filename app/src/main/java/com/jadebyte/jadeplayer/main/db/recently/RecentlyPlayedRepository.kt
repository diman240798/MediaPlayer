// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.db.recently

import androidx.lifecycle.LiveData
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayed
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayedDao


/**
 * Created by Wilberforce on 2019-09-15 at 07:39.
 */

class RecentlyPlayedRepository(private val playedDao: RecentlyPlayedDao) {

    fun fetchAll() = playedDao.fetchAllNow()

    val recentlyPlayed: LiveData<List<RecentlyPlayed>> = playedDao.fetchAll()

    suspend fun insert(recentlyPlayed: RecentlyPlayed) = playedDao.insert(recentlyPlayed)

    suspend fun trim() = playedDao.trim()

    suspend fun fetchFirst(): RecentlyPlayed? = playedDao.fetchFirst()
}
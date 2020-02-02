package com.nanicky.devteam.main.db.recently

import androidx.lifecycle.LiveData


class RecentlyPlayedRepository(private val playedDao: RecentlyPlayedDao) {

    fun fetchAll() = playedDao.fetchAllNow()

    val recentlyPlayed: LiveData<List<RecentlyPlayed>> = playedDao.fetchAll()

    suspend fun insert(recentlyPlayed: RecentlyPlayed) = playedDao.insert(recentlyPlayed)

    suspend fun trim() = playedDao.trim()

    suspend fun fetchFirst(): RecentlyPlayed? = playedDao.fetchFirst()
}
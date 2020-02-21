package com.nanicky.devteam.main.db.playlist


class PlaylistRepository(private val dao: PlaylistDao) {

    private lateinit var INSTANCE: List<PlaylistDb>


    suspend fun load() {
        val playLists: List<PlaylistDb>? = dao.fetchAllNow()

        when (playLists == null) {
            true -> createNewInstanceAndSave()
            false -> INSTANCE = playLists
        }
    }

    fun fetchAll() = dao.fetchAllNow()

    suspend fun insert(recentlyPlayed: PlaylistDb) = dao.insert(recentlyPlayed)

    suspend fun remove(id: Long) = dao.removeById(id)

    suspend fun remove(item: PlaylistDb) = dao.removeById(item.id)

    suspend fun fetchFirst(): PlaylistDb? = dao.fetchFirst()


    private suspend fun createNewInstanceAndSave() {
        INSTANCE = mutableListOf()
        dao.insertAll(INSTANCE)
    }

    fun getPlaylists() = INSTANCE
}
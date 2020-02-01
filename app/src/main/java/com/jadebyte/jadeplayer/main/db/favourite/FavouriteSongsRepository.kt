package com.jadebyte.jadeplayer.main.db.favourite

import kotlinx.coroutines.*

private const val ID = 100;

class FavouriteSongsRepository(val dao: FavouriteSongsDao) {

    private lateinit var INSTANCE: FavouriteSongs

    fun load() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            val favSongs: FavouriteSongs? = dao.fetchFirst()
            when(favSongs == null) {
                true -> createNewInstanceAndSave()
                false -> INSTANCE = favSongs
            }
        }
    }

    private suspend fun createNewInstanceAndSave() {
        INSTANCE = FavouriteSongs(
            ID,
            mutableListOf()
        )
        dao.insert(INSTANCE)
    }

    fun get() : FavouriteSongs = INSTANCE

    fun save() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            dao.insert(INSTANCE)
        }
    }
}
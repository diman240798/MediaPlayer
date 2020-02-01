package com.jadebyte.jadeplayer.main.db.favourite

import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val ID = 100;

class FavouriteSongsRepository(val dao: FavouriteSongsDao) {
    lateinit var browseTree: BrowseTree

    private lateinit var INSTANCE: FavouriteSongs

    suspend fun load() {
        val favSongs: FavouriteSongs? = dao.fetchFirst()
        when (favSongs == null) {
            true -> createNewInstanceAndSave()
            false -> INSTANCE = favSongs
        }
    }

    fun addRemove(id: String) {
        if (containsId(id)) {
            INSTANCE.ids.remove(id)
            browseTree.removeFromFavourites(id)
        } else {
            INSTANCE.ids.add(id)
            browseTree.addToFavourites(id)
        }
        save()
    }

    fun containsId(id: String): Boolean = INSTANCE.ids.contains(id)

    private fun save() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            dao.insert(INSTANCE)
        }
    }

    private suspend fun createNewInstanceAndSave() {
        INSTANCE = FavouriteSongs(
            ID,
            mutableListOf()
        )
        dao.insert(INSTANCE)
    }

}

/*fun loadAsync() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            load()
        }
    }*/
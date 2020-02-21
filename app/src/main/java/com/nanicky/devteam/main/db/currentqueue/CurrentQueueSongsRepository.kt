package com.nanicky.devteam.main.db.currentqueue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Base64
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.utils.ParcelableUtil
import com.nanicky.devteam.main.common.utils.toMediaMetadataCompat
import com.nanicky.devteam.main.common.utils.toMediaMetadataCompatDb
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

private const val ID = 100;

class CurrentQueueSongsRepository(val dao: CurrentQueueSongsDao) {
    lateinit var browseTree: BrowseTree

    private lateinit var INSTANCE: CurrentQueueSongs
    private lateinit var items: MutableList<MediaMetadataCompat>

    suspend fun load() {
        val favSongs: CurrentQueueSongs? = dao.fetchFirst()
        when (favSongs == null) {
            true -> createNewInstanceAndSave()
            false -> INSTANCE = favSongs
        }
        items = INSTANCE.items
            .map { Base64.decode(it, Base64.DEFAULT) }
            .map { ParcelableUtil.unmarshall(it, MediaMetadataCompatDb.CREATOR) }
            .map { toMediaMetadataCompat(it)!! }
            .toMutableList()
    }

    fun insert(sourceString: String) {
        if (sourceString == Constants.CURRENT_QUEUE_ROOT) return

        val mediaList = browseTree[sourceString]!!
        items = mediaList
        save(mediaList)
    }

    private fun save(mediaList: CopyOnWriteArrayList<MediaMetadataCompat>) {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            INSTANCE.items = mediaList
                .map {
                    toMediaMetadataCompatDb(
                        it
                    )
                }
                .map { ParcelableUtil.marshall(it) }
                .map { Base64.encodeToString(it, Base64.DEFAULT) }
                .toMutableList()
            dao.insert(INSTANCE)
        }
    }

    private fun createNewInstanceAndSave() {
        INSTANCE = CurrentQueueSongs(
            ID,
            mutableListOf()
        )
    }

    fun get(): List<MediaMetadataCompat>? {
        return if (::items.isInitialized) items else null
    }

    fun remove(item: MediaDescriptionCompat) {
        val index = items.indexOfFirst { it.id == item.mediaId }
        items.removeAt(index)
        INSTANCE.items.removeAt(index)
        save()
    }



    private fun save() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            dao.insert(INSTANCE)
        }
    }
}

/*fun loadAsync() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            load()
        }
    }*/
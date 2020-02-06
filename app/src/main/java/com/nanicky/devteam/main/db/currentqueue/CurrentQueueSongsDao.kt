package com.nanicky.devteam.main.db.currentqueue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentQueueSongsDao {
    @Query("SELECT * FROM current_queue_songs LIMIT 1")
    suspend fun fetchFirst(): CurrentQueueSongs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteSongs: CurrentQueueSongs)
}
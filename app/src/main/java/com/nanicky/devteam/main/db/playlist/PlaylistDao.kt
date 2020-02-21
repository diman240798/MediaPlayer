package com.nanicky.devteam.main.db.playlist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanicky.devteam.main.common.data.Constants


@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist_table")
    fun fetchAll(): LiveData<List<PlaylistDb>>

    @Query("SELECT * FROM playlist_table")
    fun fetchAllNow(): List<PlaylistDb>?

    @Query("SELECT * FROM playlist_table LIMIT 1")
    suspend fun fetchFirst(): PlaylistDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(PlaylistDb: PlaylistDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemsList: List<PlaylistDb>)

    /**
     * We want to keep a maximum of [Constants.MAX_RECENTLY_PLAYED] items in this database
     *
     * This will delete the rows whose id is greater than [Constants.MAX_RECENTLY_PLAYED]
     */
    @Query("DELETE FROM playlist_table where id = :id")
    suspend fun removeById(id: Long)

}
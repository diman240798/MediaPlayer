package com.nanicky.devteam.main.db.recently

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nanicky.devteam.main.common.data.Constants


@Dao
interface RecentlyPlayedDao {
    @Query("SELECT * FROM recently_played_table ORDER BY entryDate DESC")
    fun fetchAll(): LiveData<List<RecentlyPlayed>>

    @Query("SELECT * FROM recently_played_table ORDER BY entryDate DESC")
    fun fetchAllNow(): List<RecentlyPlayed>

    @Query("SELECT * FROM recently_played_table ORDER BY entryDate DESC LIMIT 1")
    suspend fun fetchFirst(): RecentlyPlayed?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentlyPlayed: RecentlyPlayed)

    /**
     * We want to keep a maximum of [Constants.MAX_RECENTLY_PLAYED] items in this database
     *
     * This will delete the rows whose id is greater than [Constants.MAX_RECENTLY_PLAYED]
     */
    @Query("DELETE FROM recently_played_table where id NOT IN (SELECT id from recently_played_table ORDER BY entryDate DESC LIMIT ${Constants.MAX_RECENTLY_PLAYED})")
    suspend fun trim()

}
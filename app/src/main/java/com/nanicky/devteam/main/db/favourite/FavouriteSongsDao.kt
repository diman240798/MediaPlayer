package com.nanicky.devteam.main.db.favourite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteSongsDao {
    @Query("SELECT * FROM favourite_songs LIMIT 1")
    suspend fun fetchFirst(): FavouriteSongs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteSongs: FavouriteSongs)
}
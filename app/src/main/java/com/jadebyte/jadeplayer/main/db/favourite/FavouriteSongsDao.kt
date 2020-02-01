package com.jadebyte.jadeplayer.main.db.favourite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongs

@Dao
interface FavouriteSongsDao {
    @Query("SELECT * FROM favourite_songs LIMIT 1")
    suspend fun fetchFirst(): FavouriteSongs?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteSongs: FavouriteSongs)
}
package com.nanicky.devteam.main.lyrics

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*


@Dao
interface LyricsDao {

    @Query("SELECT * FROM lyrics")
    fun getAll(): List<Lyrics>

    @Query("SELECT * FROM lyrics WHERE id = :id")
    fun getById(id: String): Lyrics

    @Insert
    fun insert(employee: Lyrics): Long

    @Update
    fun update(employee: Lyrics)

    @Delete
    fun delete(employee: Lyrics)

    @Transaction
    fun upsert(lyrics: Lyrics) {
        try {
            insert(lyrics).toLong()
        } catch (ex: SQLiteConstraintException) {
            update(lyrics)
        }
    }
}

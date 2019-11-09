// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.lyrics

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
    fun upsert(obj: Lyrics) {
        try {
            insert(obj).toLong()
        } catch (ex: SQLiteConstraintException) {
            update(obj)
        }
    }
}

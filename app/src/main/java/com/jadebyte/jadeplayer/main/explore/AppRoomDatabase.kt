// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.explore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jadebyte.jadeplayer.main.lyrics.Lyrics
import com.jadebyte.jadeplayer.main.lyrics.LyricsDao


/**
 * Created by Wilberforce on 2019-09-15 at 07:13.
 */
@Database(entities = [RecentlyPlayed::class, Lyrics::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun recentDao(): RecentlyPlayedDao
    abstract fun lyricsDao(): LyricsDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder<AppRoomDatabase>(
                    context.applicationContext, AppRoomDatabase::class.java,
                    "app_room_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
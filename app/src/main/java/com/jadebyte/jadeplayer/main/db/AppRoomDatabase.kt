// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayed
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayedDao
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongs
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongsDao
import com.jadebyte.jadeplayer.main.db.favourite.StringArrayConverter
import com.jadebyte.jadeplayer.main.lyrics.Lyrics
import com.jadebyte.jadeplayer.main.lyrics.LyricsDao


/**
 * Created by Wilberforce on 2019-09-15 at 07:13.
 */
@Database(entities = [RecentlyPlayed::class, Lyrics::class, FavouriteSongs::class], version = 1)
@TypeConverters(value = [StringArrayConverter::class])
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun favouriteSongsDao(): FavouriteSongsDao
    abstract fun recentDao(): RecentlyPlayedDao
    abstract fun lyricsDao(): LyricsDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder<AppRoomDatabase>(
                    context.applicationContext, AppRoomDatabase::class.java,
                    "app_room_database_1"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
package com.nanicky.devteam.main.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nanicky.devteam.main.db.converter.StringArrayConverter
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongs
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongsDao
import com.nanicky.devteam.main.db.favourite.FavouriteSongs
import com.nanicky.devteam.main.db.favourite.FavouriteSongsDao
import com.nanicky.devteam.main.db.playlist.PlaylistDao
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.recently.RecentlyPlayed
import com.nanicky.devteam.main.db.recently.RecentlyPlayedDao
import com.nanicky.devteam.main.lyrics.Lyrics
import com.nanicky.devteam.main.lyrics.LyricsDao


@Database(
    entities = [
        RecentlyPlayed::class, Lyrics::class, FavouriteSongs::class,
        CurrentQueueSongs::class, Playlist::class
    ],
    version = 1
)
@TypeConverters(value = [StringArrayConverter::class])
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun favouriteSongsDao(): FavouriteSongsDao
    abstract fun recentDao(): RecentlyPlayedDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun currentQueueSongsDao(): CurrentQueueSongsDao
    abstract fun playlistDao(): PlaylistDao

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
                    "app_room_database_2"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
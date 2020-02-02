package com.nanicky.devteam.main.playlist

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.SongsRepository

class PlaylistSongsRepository(application: Application) : SongsRepository(application) {

    override fun transform(cursor: Cursor): Song {
        return Song(cursor, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)))
    }
}
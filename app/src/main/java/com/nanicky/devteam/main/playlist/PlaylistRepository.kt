package com.nanicky.devteam.main.playlist

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.nanicky.devteam.main.common.data.MediaStoreRepository

class PlaylistRepository(application: Application) : MediaStoreRepository<Playlist>(application) {

    override fun transform(cursor: Cursor): Playlist = Playlist(cursor)

    @WorkerThread
    fun fetchSongCount(playlistId: Long): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
        val selectionArgs = arrayOf("0")
        val cursor = query(uri, projection, selection, selectionArgs)
        return cursor?.count ?: 0
    }
}
package com.jadebyte.jadeplayer.main.playback.mediasource

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.playlist.Playlist

class PlaylistMediaSource {

    val playlists = mutableListOf<Playlist>()

    fun load(context: Context) {

        val cursor =
            context.contentResolver.query(
                basePlaylistUri,
                basePlaylistProjection,
                null,
                null,
                sortOrder
            )
        cursor?.use {
            while (it.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME))
                val modified =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.DATE_MODIFIED))

                val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
                val projection = arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID)
                val selection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"
                val selectionArgs = arrayOf(id.toString())
                val cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)

                val songIds = mutableListOf<String>()
                cursor?.use {
                    while (it.moveToNext()) {
                        val columnIndex =
                            cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
                        val songId = columnIndex
                            .takeIf { columnIndex != -1 }
                            .let { cursor.getString(columnIndex) }
                        songId?.let { songIds.add(songId) }
                    }
                }
                playlists.add(Playlist(id, name, modified, songIds = songIds))
            }
        }
    }

}


val basePlaylistProjection = arrayOf(
    MediaStore.Audio.Playlists._ID,
    MediaStore.Audio.Playlists.NAME,
    MediaStore.Audio.Playlists.DATE_MODIFIED
)

val basePlaylistUri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

var sortOrder: String? = "${MediaStore.Audio.Playlists.DATE_MODIFIED} COLLATE NOCASE DESC"
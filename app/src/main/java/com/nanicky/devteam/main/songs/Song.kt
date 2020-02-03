package com.nanicky.devteam.main.songs

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.common.utils.ImageUtils
import com.nanicky.devteam.main.playback.*
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Song(
    override val id: String,
    val title: String,
    val titleKey: String = "",
    val album: Album,
    val path: String,
    val duration: Long,
    val artPath: String,
    var isCurrent: Boolean = false,
    var selected: Boolean = false,
    var audioId: Long? = null
) : Model(), Parcelable {
    constructor(cursor: Cursor) : this(
        id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
        titleKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY)),
        album = Album(cursor, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))),
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
        artPath = ImageUtils.getAlbumArtUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString()
    )

    constructor(cursor: Cursor, audioId: Long?) : this(
        id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID)),
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)),
        titleKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE_KEY)),
        album = Album(cursor, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID))),
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)),
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)),
        artPath = ImageUtils.getAlbumArtUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID))).toString(),
        audioId = audioId
    )

    constructor(data: MediaMetadataCompat) : this(
        id = data.id ?: "",
        title = data.title ?: "",
        album = Album(data),
        path = data.mediaUri.toString(),
        duration = data.duration,
        artPath = ImageUtils.getAlbumArtUri(data.albumId).toString()
    )
}
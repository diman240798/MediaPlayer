package com.nanicky.devteam.main.albums

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.common.urlEncoded
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    override var id: String = "",
    val name: String,
    val artist: String
//    val tracks: Long? = 0,
//    val year: String? = "" // TODO:
) : Model(), Parcelable {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
        artist = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
//        year = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
//        tracks = data.getInt(data.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)).toLong(),
        id = data.getString(data.getColumnIndex(MediaStore.Audio.Albums._ID))
    )

    constructor(cursor: Cursor, id: String) : this(
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
        id = id
    )

    constructor(data: MediaMetadataCompat) : this(
        name = data.album ?: "",
        artist = data.artist ?: "",
//        tracks = data.trackNumber,
        id = data.albumId.urlEncoded
    )
}

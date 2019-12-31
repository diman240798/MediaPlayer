// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.*
import kotlinx.android.parcel.Parcelize

/**
 * Created by Wilberforce on 16/04/2019 at 00:49.
 */
@Parcelize
data class Album(
    override val id: Long = 0,
    val name: String,
    val artist: String,
    val key: String,
    val tracks: Long? = 0,
    val year: String? = ""
) : Model(), Parcelable {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
        artist = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
        year = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)),
        tracks = data.getInt(data.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)).toLong(),
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Albums._ID)),
        key = data.getString(data.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY))
    )

    constructor(cursor: Cursor, id: Long) : this(
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
        id = id,
        key = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY))
    )

    constructor(data: MediaMetadataCompat) : this(
        name = data.getString(MediaStore.Audio.Albums.ALBUM),
        artist = data.getString(MediaStore.Audio.Albums.ARTIST),
        year = data.getString(MediaStore.Audio.Albums.FIRST_YEAR),
        tracks = data.getLong(MediaStore.Audio.Albums.NUMBER_OF_SONGS),
        id = data.getLong(MediaStore.Audio.Albums._ID),
        key = data.getString(MediaStore.Audio.Albums.ALBUM_KEY)
    )
}

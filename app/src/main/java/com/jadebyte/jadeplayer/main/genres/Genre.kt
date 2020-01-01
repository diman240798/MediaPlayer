// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.id
import com.jadebyte.jadeplayer.main.playback.title
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(override val id: String, val name: String) : Model(), Parcelable {

    constructor(cursor: Cursor) : this(
        id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres._ID)),
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME))
    )

    constructor(data: MediaMetadataCompat) : this(
        id = data.id ?: "",
        name = data.title ?: "UNKNOWN"
    )
}
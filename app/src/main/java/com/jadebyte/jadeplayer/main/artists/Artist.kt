// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.title
import kotlinx.android.parcel.Parcelize


/**
 * Created by Wilberforce on 2019-04-25 at 00:46.
 */
@Parcelize
data class Artist(
    override val id: Long,
    val name: String,
    val songsCount: Long,
    val albumsCount: Long
) : Model(),
    Parcelable {

    constructor(data: Cursor) : this(
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
        songsCount = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)),
        albumsCount = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)),
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists._ID))
    )

    constructor(data: MediaMetadataCompat) : this(
        name = data.title!!,
        songsCount = data.getLong(MediaStore.Audio.Artists.NUMBER_OF_TRACKS),
        albumsCount = data.getLong(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS),
        id = data.getLong(MediaStore.Audio.Artists._ID)
    )

}
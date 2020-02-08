package com.nanicky.devteam.main.artists

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.artist
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.title
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Artist(
    override val id: String,
    val name: String
) : Model(),
    Parcelable {

    constructor(data: Cursor) : this(
        id = data.getLong(data.getColumnIndex(MediaStore.Audio.Artists._ID)).toString(),
        name = data.getString(data.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
    )

    constructor(data: MediaMetadataCompat) : this(
        id = data.id!!,
        name = data.title ?: "No Artist"
    )

}
package com.nanicky.devteam.main.artists

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.title
import kotlinx.android.parcel.Parcelize


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
package com.nanicky.devteam.main.albums

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.common.urlEncoded
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.album
import com.nanicky.devteam.main.playback.albumId
import com.nanicky.devteam.main.playback.artist
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    override var id: String = "",
    val name: String,
    val artist: String
//    val tracks: Long? = 0,
//    val year: String? = "" // TODO:
) : Model(), Parcelable {

    constructor(data: MediaMetadataCompat) : this(
        name = data.album ?: "",
        artist = data.artist ?: "",
//        tracks = data.trackNumber,
        id = data.albumId.urlEncoded
    )
}

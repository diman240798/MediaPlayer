package com.nanicky.devteam.main.artists

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.title
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Artist(
    override val id: String,
    val name: String
) : Model(),
    Parcelable {

    constructor(data: MediaMetadataCompat) : this(
        id = data.id!!,
        name = data.title ?: "No Artist"
    )

}
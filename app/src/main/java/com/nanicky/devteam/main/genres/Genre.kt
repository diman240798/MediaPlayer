package com.nanicky.devteam.main.genres

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.title
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(override val id: String, val name: String) : Model(), Parcelable {

    constructor(data: MediaMetadataCompat) : this(
        id = data.id ?: "",
        name = data.title ?: "UNKNOWN"
    )


    fun getUniqueKey(): String = "${name}_genre_$id"
}
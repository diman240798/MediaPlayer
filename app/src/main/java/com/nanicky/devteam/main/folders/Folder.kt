package com.nanicky.devteam.main.folders

import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.mediaUri
import com.nanicky.devteam.main.playback.title

class Folder(
    override val id: String,
    val name: String,
    val path: String
) : Model() {
    constructor(data: MediaMetadataCompat) : this(
        id = data.id!!,
        name = data.title!!,
        path = data.mediaUri.toString()
    )
}

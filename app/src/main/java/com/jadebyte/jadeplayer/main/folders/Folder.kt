package com.jadebyte.jadeplayer.main.folders

import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.id
import com.jadebyte.jadeplayer.main.playback.mediaUri
import com.jadebyte.jadeplayer.main.playback.title
import com.jadebyte.jadeplayer.main.songs.Song

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

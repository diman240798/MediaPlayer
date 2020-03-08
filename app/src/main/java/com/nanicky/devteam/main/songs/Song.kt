package com.nanicky.devteam.main.songs

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.common.utils.ImageUtils
import com.nanicky.devteam.main.playback.*
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Song(
    override val id: String,
    val title: String,
    val titleKey: String = "",
    val album: Album,
    val path: String,
    val duration: Long,
    val artPath: String,
    var isCurrent: Boolean = false,
    var selected: Boolean = false,
    var audioId: Long? = null
) : Model(), Parcelable {

    constructor(data: MediaMetadataCompat) : this(
        id = data.id ?: "",
        title = data.title ?: "",
        album = Album(data),
        path = data.mediaUri.toString(),
        duration = data.duration,
        artPath = ImageUtils.getAlbumArtUri(data.albumId).toString()
    )
}
package com.nanicky.devteam.main.db.currentqueue

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.*
import kotlinx.android.parcel.Parcelize

fun toMediaMetadataCompatDb(item: MediaMetadataCompat): MediaMetadataCompatDb {
    return MediaMetadataCompatDb(
        item.id!!,
        item.title,
        item.artist,
        item.album,
        item.albumId,
        item.albumArtUri.toString(),
        item.mediaUri.toString(),
        item.flag,
        item.displayTitle,
        item.displaySubtitle,
        item.displayDescription,
        item.downloadStatus
    )
}

fun toMediaMetadataCompat(item: MediaMetadataCompatDb): MediaMetadataCompat? {
    return MediaMetadataCompat.Builder().apply {
        id = item.id
        title = item.title
        artist = item.artist
        album = item.album
        albumId = item.albumId
        albumArtUri = item.albumArtUri

        mediaUri = item.mediaUri
        albumArtUri = item.albumArtUri
        flag = item.flag
        displayTitle = item.displayTitle
        displaySubtitle = item.displaySubtitle
        displayDescription = item.displayDescription
        downloadStatus = item.downloadStatus
    }.build()
}



@Parcelize
data class MediaItemDataDb(
    override val id: String,
    val title: String,
    val subtitle: String, // Artist
    val description: MediaDescriptionCompatDb, //Album
    val albumArtUri: String?,
    val isBrowsable: Boolean,
    var isPlaying: Boolean,
    var isBuffering: Boolean,
    var duration: Long = 0L
) : Model(), Parcelable

@Parcelize
class MediaDescriptionCompatDb(
    val mediaId: String,
    val mediaUri: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ByteArray,
    val iconUri: String
) : Parcelable

package com.nanicky.devteam.main.common.utils

import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.db.currentqueue.MediaMetadataCompatDb
import com.nanicky.devteam.main.playback.*

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
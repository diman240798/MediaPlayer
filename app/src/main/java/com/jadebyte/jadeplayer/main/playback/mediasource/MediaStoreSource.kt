// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback.mediasource

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.playback.from
import com.jadebyte.jadeplayer.main.playback.id
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * Created by Wilberforce on 2019-08-22 at 01:00.
 *
 * Source of [MediaMetadataCompat] objects created from songs in the MediaStore
 */
class MediaStoreSource(val context: Context) {


    private lateinit var lastId: String

    fun loadNew() {

    }

    fun load(): Flow<MediaMetadataCompat> =
        load(baseSongUri, songsProjection, basicSongsSelection, basicSongsSelectionArgs, basicSongsOrder)

    fun load(
        uri: Uri,
        songsProjection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    )
            : Flow<MediaMetadataCompat> = flow {

        val art = ImageUtils.getBitmapFromVectorDrawable(context, R.drawable.thumb_circular_default)

        val cursor =
            context.contentResolver.query(uri, songsProjection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val count = it.count.toLong()
            while (it.moveToNext()) {
                // Block on downloading artwork.
                val metadata = MediaMetadataCompat.Builder().from(it, count, art)
                val build = metadata.build()
                build.description.extras?.putAll(build.bundle)
                if (it.isLast) lastId = build.id.toString()
                emit(build)
            }
        }
    }

}


// Sort with the title in ascending case-insensitive order
const val basicSongsOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

const val basicSongsSelection = "${MediaStore.Audio.Media.IS_MUSIC} != ?"

val baseSongUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

val basicSongsSelectionArgs get() = arrayOf(basicSongsSelectionArg)

const val basicSongsSelectionArg = "0"

private val songsProjection = arrayOf(
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TRACK,
    MediaStore.Audio.Media._ID
)

const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

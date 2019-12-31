// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback.mediasource

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.ImageUtils
import com.jadebyte.jadeplayer.main.playback.from


/**
 * Created by Wilberforce on 2019-08-22 at 01:00.
 *
 * Source of [MediaMetadataCompat] objects created from songs in the MediaStore
 */
open class MediaStoreSource(
    val context: Context, val uri: Uri,
    val selection: String,
    val selectionArgs: Array<String>,
    val sortOrder: String
) : AbstractMusicSource() {
    private var catalog: List<MediaMetadataCompat> = emptyList()

    init {
        state =
            STATE_INITIALIZING

    }

    override fun load() {
        updateCatalog()?.let {
            catalog = it
            state =
                STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    override fun iterator() = catalog.iterator()


    private fun updateCatalog(): List<MediaMetadataCompat>? {

        val art = ImageUtils.getBitmapFromVectorDrawable(context, R.drawable.thumb_circular_default)

        val results = mutableListOf<MediaMetadataCompat>()
        val cursor =
            context.contentResolver.query(uri,
                songsProjection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val count = it.count.toLong()
            while (it.moveToNext()) {
                // Block on downloading artwork.
                val metadata = MediaMetadataCompat.Builder().from(it, count, art)
                val build = metadata.build()
                build.description.extras?.putAll(build.bundle)
                results.add(build)
            }
        }

        return results

    }

}

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

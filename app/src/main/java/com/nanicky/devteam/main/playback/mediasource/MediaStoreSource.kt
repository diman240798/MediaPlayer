package com.nanicky.devteam.main.playback.mediasource

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.utils.ImageUtils
import com.nanicky.devteam.main.playback.from
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.mediaUri
import com.nanicky.devteam.main.playback.title
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 *
 * Source of [MediaMetadataCompat] objects created from songs in the MediaStore
 */
class MediaStoreSource {


    @Volatile var lastId: Int = -1

    fun loadNew(context: Context): Flow<MediaMetadataCompat> {
        Log.d("Loading new songs: ", "id > then $lastId")
        return load(context, baseSongUri, songsProjection,
            "${MediaStore.Audio.Media._ID} > ?", arrayOf(lastId.toString()),
            basicSongsOrder, true)
    }

    fun load(context: Context): Flow<MediaMetadataCompat> =
        load(context, baseSongUri, songsProjection, null, null, basicSongsOrder)

    fun load(
        context: Context,
        uri: Uri,
        songsProjection: Array<String>,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String,
        onlyNew: Boolean = false
    )
            : Flow<MediaMetadataCompat> = flow {

        val art = ImageUtils.getBitmapFromVectorDrawable(context, R.drawable.thumb_circular_default)

        val cursor = context.contentResolver.query(uri, songsProjection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val count = it.count.toLong()
            while (it.moveToNext()) {
                // Block on downloading artwork.
                val metadata = MediaMetadataCompat.Builder().from(it, count, art)
                val build = metadata.build()
                build.description.extras?.putAll(build.bundle)
                val id = build.id
                // Workout Ids
                val idInt = id?.toInt() ?: continue
                Log.d("Loading song:", "id: $id, lastId: $lastId")
                if (idInt >= lastId) {
                    if (onlyNew && idInt == lastId) continue
                    lastId = idInt
                }
                Log.d("Loading song: ", "id: ${id}, title: ${build.title}, mediaUri: ${build.mediaUri}")
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

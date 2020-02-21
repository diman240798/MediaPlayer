package com.nanicky.devteam.main.playlist

import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.common.dp
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.id
import com.nanicky.devteam.main.playback.title
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Playlist(
    override val id: Long,
    var name: String,
    val modified: Long = 0,
    var songsCount: Int = 0,
    var selected: Boolean = false,
    var songIds : MutableList<String> = mutableListOf()
) : Model(), Parcelable {

    constructor(cursor: Cursor) : this(
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)),
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)),
        modified = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.DATE_MODIFIED))
    )

    constructor(data: MediaMetadataCompat) : this(
        id = data.id?.toLong() ?: 0,
        name = data.title ?: ""
    )

    constructor(p: Playlist) : this(id = p.id, name = p.name, modified = p.modified, songsCount = p.songsCount)

    constructor(id: Long) : this(id = id, name = "", modified = 0)

    /**
     *  When [width] is more than [Constants.MAX_MODEL_IMAGE_THUMB_WIDTH], we'll change the value of any of this
     *  playlist's fields to force Glide to use a cache key different from an unmodified playlist. The reason for this
     *  is that we are using a different loading algorithm for ImageViews with with more
     *  than [Constants.MAX_MODEL_IMAGE_THUMB_WIDTH]. See [PlaylistModelLoader] for the algorithm
     *
     *  @param width the width of the ImageViw in pixels
     *  @return this playlist
     */
    fun modForViewWidth(width: Int): Playlist {
        if (width.dp > Constants.MAX_MODEL_IMAGE_THUMB_WIDTH) {
            name = "$name$id$songsCount"
        }
        return this
    }

}
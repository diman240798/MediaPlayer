package com.nanicky.devteam.main.db.currentqueue

import android.os.Parcel
import android.os.Parcelable

data class MediaMetadataCompatDb(
    val id: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumId: Long,
    val albumArtUri: String?,
    val mediaUri: String?,
    val flag: Int,
    val displayTitle: String?,
    val displaySubtitle: String?,
    val displayDescription: String?,
    val downloadStatus: Long
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(artist)
        writeString(album)
        writeLong(albumId)
        writeString(albumArtUri)
        writeString(mediaUri)
        writeInt(flag)
        writeString(displayTitle)
        writeString(displaySubtitle)
        writeString(displayDescription)
        writeLong(downloadStatus)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MediaMetadataCompatDb> =
            object : Parcelable.Creator<MediaMetadataCompatDb> {
                override fun createFromParcel(source: Parcel): MediaMetadataCompatDb =
                    MediaMetadataCompatDb(source)

                override fun newArray(size: Int): Array<MediaMetadataCompatDb?> = arrayOfNulls(size)
            }
    }
}
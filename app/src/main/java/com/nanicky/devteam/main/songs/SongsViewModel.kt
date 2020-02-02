package com.nanicky.devteam.main.songs

import android.app.Application
import android.provider.MediaStore
import com.nanicky.devteam.main.common.callbacks.SongSuplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

open class SongsViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier())

val baseSongsProjection = arrayOf(
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TRACK,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ARTIST_ID,
    MediaStore.Audio.Media.TITLE_KEY,
    MediaStore.Audio.Media.ALBUM_KEY,
    MediaStore.Audio.Media._ID
)
// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree

/**
 * Created by Wilberforce on 19/04/2019 at 15:06.
 */
open class SongsViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier()) {}

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
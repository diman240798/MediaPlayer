// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.*


/**
 * Created by Wilberforce on 2019-04-21 at 12:47.
 */
class AlbumSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier()) {

    override var selection: String? = "$basicSongsSelection AND ${MediaStore.Audio.Media.ALBUM_ID} = ?"

    override var sortOrder: String? = "${MediaStore.Audio.Media.TRACK} COLLATE NOCASE ASC"

    override var uri: Uri = baseSongUri
    override var repository: MediaStoreRepository<Song> = SongsRepository(application)
}
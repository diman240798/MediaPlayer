// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import android.net.Uri
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsRepository
import com.jadebyte.jadeplayer.main.songs.baseSongUri

class GenreSongsViewModel(application: Application, browseTree: BrowseTree) :
    BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier()) {

    override var uri: Uri = baseSongUri
    override var repository: MediaStoreRepository<Song> = SongsRepository(application)
}
// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.Song

class GenreSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier())
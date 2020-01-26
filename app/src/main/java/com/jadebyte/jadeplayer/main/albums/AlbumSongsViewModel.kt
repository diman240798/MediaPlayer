// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums

import android.app.Application
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.Song


/**
 * Created by Wilberforce on 2019-04-21 at 12:47.
 */
class AlbumSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier())
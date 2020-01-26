package com.jadebyte.jadeplayer.main.folders

import android.app.Application
import com.jadebyte.jadeplayer.main.common.callbacks.SongSuplier
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.Song

class FolderSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier()) {
    var folder: Folder? = null
}

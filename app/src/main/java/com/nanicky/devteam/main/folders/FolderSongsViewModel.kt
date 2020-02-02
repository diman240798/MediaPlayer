package com.nanicky.devteam.main.folders

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.SongSuplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song

class FolderSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier()) {
    var folder: Folder? = null
}

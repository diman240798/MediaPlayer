package com.jadebyte.jadeplayer.main.folders

import android.app.Application
import com.jadebyte.jadeplayer.main.common.callbacks.FolderSupplier
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree

class FoldersViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Folder>(
    application,
    browseTree,
    FolderSupplier()
)
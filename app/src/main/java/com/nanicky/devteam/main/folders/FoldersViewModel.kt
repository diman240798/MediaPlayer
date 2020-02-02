package com.nanicky.devteam.main.folders

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.FolderSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

class FoldersViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Folder>(
    application,
    browseTree,
    FolderSupplier()
)
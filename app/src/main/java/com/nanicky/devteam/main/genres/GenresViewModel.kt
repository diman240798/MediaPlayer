package com.nanicky.devteam.main.genres

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.GenreSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

class GenresViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Genre>(
    application,
    browseTree,
    GenreSupplier()
)

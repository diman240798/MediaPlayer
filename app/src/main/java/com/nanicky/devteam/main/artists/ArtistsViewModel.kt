package com.nanicky.devteam.main.artists

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.ArtistSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

class ArtistsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Artist>(application, browseTree, ArtistSupplier())
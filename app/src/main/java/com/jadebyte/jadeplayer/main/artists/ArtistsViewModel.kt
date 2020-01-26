// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.artists

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.callbacks.ArtistSupplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree

/**
 * Created by Wilberforce on 2019-04-25 at 00:57.
 */
class ArtistsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Artist>(application, browseTree, ArtistSupplier())
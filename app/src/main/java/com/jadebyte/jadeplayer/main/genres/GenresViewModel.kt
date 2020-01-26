// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.genres

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.common.callbacks.GenreSupplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.view.BaseMediaStoreViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree

class GenresViewModel(
    application: Application,
    browseTree: BrowseTree
) : BaseMediaStoreViewModel<Genre>(
    application,
    browseTree,
    GenreSupplier()
)

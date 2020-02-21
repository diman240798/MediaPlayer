package com.nanicky.devteam.main.playlist

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.nanicky.devteam.main.common.callbacks.PlaylistSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.db.playlist.PlaylistDb
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

open class PlaylistFragmentViewModel(
    application: Application, browseTree: BrowseTree
) : BaseMediaStoreViewModel<PlaylistDb>(application, browseTree, PlaylistSupplier()) {

    fun reverseSelection(index: Int): Boolean {
        return data.value?.let {
            if (it.size > index) {
                it[index].selected = !it[index].selected
                true
            } else false
        } ?: false

    }

}

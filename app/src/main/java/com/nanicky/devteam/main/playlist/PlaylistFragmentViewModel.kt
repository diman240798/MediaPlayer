package com.nanicky.devteam.main.playlist

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.PlaylistSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import java.util.*

open class PlaylistFragmentViewModel(
    application: Application, browseTree: BrowseTree
) : BaseMediaStoreViewModel<Playlist>(application, browseTree, PlaylistSupplier()) {

    fun reverseSelection(index: Int): Boolean {
        return data.value?.let {
            if (it.size > index) {
                it[index].selected = !it[index].selected
                true
            } else false
        } ?: false

    }

    override fun deliverResult(items: List<Playlist>) {
        data.value = Collections.emptyList()
        super.deliverResult(items)
    }

}

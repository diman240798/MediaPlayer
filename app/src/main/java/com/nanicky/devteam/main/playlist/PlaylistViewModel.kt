package com.nanicky.devteam.main.playlist

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.nanicky.devteam.main.common.callbacks.PlaylistSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

open class PlaylistViewModel(
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

}

val basePlaylistProjection = arrayOf(
    MediaStore.Audio.Playlists._ID,
    MediaStore.Audio.Playlists.NAME,
    MediaStore.Audio.Playlists.DATE_MODIFIED
)

val basePlaylistUri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

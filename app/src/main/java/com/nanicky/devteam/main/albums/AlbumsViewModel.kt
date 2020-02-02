package com.nanicky.devteam.main.albums

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.nanicky.devteam.main.common.callbacks.AlbumSupplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree

open class AlbumsViewModel(application: Application, browseTree: BrowseTree) :
    BaseMediaStoreViewModel<Album>(application, browseTree, AlbumSupplier()) {



}

val baseAlbumProjection = arrayOf(
    MediaStore.Audio.Albums.ALBUM,
    MediaStore.Audio.Albums.ARTIST,
    MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    MediaStore.Audio.Albums._ID,
    MediaStore.Audio.Albums.FIRST_YEAR,
    MediaStore.Audio.Albums.ALBUM_KEY
)

val baseAlbumUri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

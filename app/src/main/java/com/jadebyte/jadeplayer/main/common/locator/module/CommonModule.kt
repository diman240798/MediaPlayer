// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.locator.module

import android.content.ComponentName
import android.preference.PreferenceManager
import com.jadebyte.jadeplayer.main.albums.AlbumSongsViewModel
import com.jadebyte.jadeplayer.main.albums.AlbumsViewModel
import com.jadebyte.jadeplayer.main.artists.ArtistAlbumsViewModel
import com.jadebyte.jadeplayer.main.artists.ArtistsViewModel
import com.jadebyte.jadeplayer.main.explore.ExploreViewModel
import com.jadebyte.jadeplayer.main.folders.FolderSongsViewModel
import com.jadebyte.jadeplayer.main.folders.FoldersViewModel
import com.jadebyte.jadeplayer.main.genres.GenreSongsViewModel
import com.jadebyte.jadeplayer.main.genres.GenresViewModel
import com.jadebyte.jadeplayer.main.navigation.NavViewModel
import com.jadebyte.jadeplayer.main.playback.MediaSessionConnection
import com.jadebyte.jadeplayer.main.playback.PlaybackService
import com.jadebyte.jadeplayer.main.playback.PlaybackViewModel
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.playback.mediasource.MediaStoreSource
import com.jadebyte.jadeplayer.main.playback.mediasource.MediaUpdateNotifier
import com.jadebyte.jadeplayer.main.playback.mediasource.PlaylistMediaSource
import com.jadebyte.jadeplayer.main.playlist.AddSongsToPlaylistsViewModel
import com.jadebyte.jadeplayer.main.playlist.PlaylistSongsEditorViewModel
import com.jadebyte.jadeplayer.main.playlist.PlaylistSongsViewModel
import com.jadebyte.jadeplayer.main.playlist.PlaylistViewModel
import com.jadebyte.jadeplayer.main.search.SearchViewModel
import com.jadebyte.jadeplayer.main.songs.SongsMenuBottomSheetDialogFragmentViewModel
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import com.jadebyte.jadeplayer.main.web.WebFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * Created by Wilberforce on 2019-08-21 at 22:43.
 *  Module which provides all required common dependencies
 */

val commonModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(get()) }
    single {
        MediaSessionConnection.getInstance(
            get(),
            ComponentName(get(), PlaybackService::class.java),
            get()
        )
    }

    viewModel { NavViewModel(get()) }
    viewModel { PlaybackViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SongsMenuBottomSheetDialogFragmentViewModel() }
    viewModel { WebFragmentViewModel() }

    // metadata
    single { PlaylistMediaSource() }
    single { MediaStoreSource() }
    single { MediaUpdateNotifier() }
    single { BrowseTree(get(),  get(), get(), get()) }
    // metadata VMs
    viewModel { FolderSongsViewModel(get(), get()) }
    viewModel { FoldersViewModel(get(), get()) }
    viewModel { SongsViewModel(get(), get()) }
    viewModel { GenresViewModel(get(), get()) }
    viewModel { AlbumSongsViewModel(get(), get()) }
    viewModel { GenreSongsViewModel(get(), get()) }
    viewModel { ArtistsViewModel(get(), get()) }
    viewModel { AlbumsViewModel(get(), get()) }
    viewModel { ArtistAlbumsViewModel(get(), get()) }
    viewModel { ExploreViewModel(get(), get()) }
    viewModel { PlaylistViewModel(get(), get()) }
    viewModel { AddSongsToPlaylistsViewModel(get(), get()) }
    viewModel { PlaylistSongsViewModel(get(), get()) }
    viewModel { PlaylistSongsEditorViewModel(get(), get()) }
}
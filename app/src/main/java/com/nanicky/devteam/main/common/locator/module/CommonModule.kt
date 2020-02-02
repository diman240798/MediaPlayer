package com.nanicky.devteam.main.common.locator.module

import android.content.ComponentName
import android.preference.PreferenceManager
import com.nanicky.devteam.main.albums.AlbumSongsViewModel
import com.nanicky.devteam.main.albums.AlbumsViewModel
import com.nanicky.devteam.main.artists.ArtistAlbumsViewModel
import com.nanicky.devteam.main.artists.ArtistsViewModel
import com.nanicky.devteam.main.db.AppRoomDatabase
import com.nanicky.devteam.main.db.favourite.FavouriteSongsRepository
import com.nanicky.devteam.main.explore.ExploreViewModel
import com.nanicky.devteam.main.favourite.FavouriteSongsViewModel
import com.nanicky.devteam.main.folders.FolderSongsViewModel
import com.nanicky.devteam.main.folders.FoldersViewModel
import com.nanicky.devteam.main.genres.GenreSongsViewModel
import com.nanicky.devteam.main.genres.GenresViewModel
import com.nanicky.devteam.main.navigation.NavViewModel
import com.nanicky.devteam.main.playback.MediaSessionConnection
import com.nanicky.devteam.main.playback.PlaybackService
import com.nanicky.devteam.main.playback.PlaybackViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.playback.mediasource.MediaStoreSource
import com.nanicky.devteam.main.playback.mediasource.MediaUpdateNotifier
import com.nanicky.devteam.main.playback.mediasource.PlaylistMediaSource
import com.nanicky.devteam.main.playlist.AddSongsToPlaylistsViewModel
import com.nanicky.devteam.main.playlist.PlaylistSongsEditorViewModel
import com.nanicky.devteam.main.playlist.PlaylistSongsViewModel
import com.nanicky.devteam.main.playlist.PlaylistViewModel
import com.nanicky.devteam.main.search.SearchViewModel
import com.nanicky.devteam.main.songs.SongsMenuBottomSheetDialogFragmentViewModel
import com.nanicky.devteam.main.songs.SongsViewModel
import com.nanicky.devteam.main.web.WebFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
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
    viewModel { FavouriteSongsViewModel(get(), get()) }
    viewModel { SongsMenuBottomSheetDialogFragmentViewModel(get()) }
    viewModel { WebFragmentViewModel() }

    // metadata
    single { PlaylistMediaSource() }
    single { FavouriteSongsRepository(AppRoomDatabase.getDatabase(get()).favouriteSongsDao()) }
    single { MediaStoreSource() }
    single { MediaUpdateNotifier() }
    single { BrowseTree(get(),  get(), get(), get(), get()) }
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
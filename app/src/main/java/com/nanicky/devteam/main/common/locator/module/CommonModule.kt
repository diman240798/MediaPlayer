package com.nanicky.devteam.main.common.locator.module

import android.content.ComponentName
import android.preference.PreferenceManager
import com.nanicky.devteam.main.albums.AlbumSongsViewModel
import com.nanicky.devteam.main.albums.AlbumsViewModel
import com.nanicky.devteam.main.artists.ArtistAlbumsViewModel
import com.nanicky.devteam.main.artists.ArtistsViewModel
import com.nanicky.devteam.main.db.AppRoomDatabase
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongsRepository
import com.nanicky.devteam.main.db.favourite.FavouriteSongsRepository
import com.nanicky.devteam.main.db.playlist.PlaylistRepository
import com.nanicky.devteam.main.equalizer.EqualizerInitializer
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
import com.nanicky.devteam.main.playlist.*
import com.nanicky.devteam.main.search.SearchViewModel
import com.nanicky.devteam.main.settings.ColorChangeSharedObject
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

    viewModel { WritePlaylistViewModel(get(), get(), get()) }
    viewModel { NavViewModel(get()) }
    viewModel { PlaybackViewModel(get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { FavouriteSongsViewModel(get(), get()) }
    viewModel { SongsMenuBottomSheetDialogFragmentViewModel(get()) }
    viewModel { WebFragmentViewModel() }

    single { EqualizerInitializer(get()) }

    // metadata
    single { PlaylistRepository(AppRoomDatabase.getDatabase(get()).playlistDao()) }
    single { FavouriteSongsRepository(AppRoomDatabase.getDatabase(get()).favouriteSongsDao()) }
    single { CurrentQueueSongsRepository(AppRoomDatabase.getDatabase(get()).currentQueueSongsDao()) }
    single { MediaStoreSource() }
    single { MediaUpdateNotifier() }
    single { BrowseTree(get(), get(), get(), get(), get(), get()) }
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
    viewModel { PlaylistFragmentViewModel(get(), get()) }
    viewModel { AddSongsToPlaylistsViewModel(get(), get(), get()) }
    viewModel { PlaylistSongsViewModel(get(), get()) }
    viewModel { PlaylistSongsEditorViewModel(get(), get(), get()) }
    // colorTheme change
    single { ColorChangeSharedObject() }
}
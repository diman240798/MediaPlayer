// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.locator.module

import android.content.ComponentName
import android.preference.PreferenceManager
import com.jadebyte.jadeplayer.main.navigation.NavViewModel
import com.jadebyte.jadeplayer.main.playback.MediaSessionConnection
import com.jadebyte.jadeplayer.main.playback.PlaybackService
import com.jadebyte.jadeplayer.main.playback.PlaybackViewModel
import com.jadebyte.jadeplayer.main.search.SearchViewModel
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
}
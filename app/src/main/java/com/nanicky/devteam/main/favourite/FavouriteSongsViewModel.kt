package com.nanicky.devteam.main.favourite

import android.app.Application
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.SongsViewModel

class FavouriteSongsViewModel(
    application: Application,
    browseTree: BrowseTree
) : SongsViewModel(application, browseTree)
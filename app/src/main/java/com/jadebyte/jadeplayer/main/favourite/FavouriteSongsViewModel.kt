package com.jadebyte.jadeplayer.main.favourite

import android.app.Application
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.songs.SongsViewModel

class FavouriteSongsViewModel(
    application: Application,
    browseTree: BrowseTree
) : SongsViewModel(application, browseTree)
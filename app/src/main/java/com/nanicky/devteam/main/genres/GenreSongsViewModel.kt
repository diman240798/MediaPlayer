package com.nanicky.devteam.main.genres

import android.app.Application
import com.nanicky.devteam.main.common.callbacks.SongSuplier
import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.songs.Song

class GenreSongsViewModel(application: Application, browseTree: BrowseTree) : BaseMediaStoreViewModel<Song>(application, browseTree, SongSuplier())
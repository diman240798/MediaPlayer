package com.nanicky.devteam.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.utils.ViewUtils
import com.nanicky.devteam.main.db.favourite.FavouriteSongsRepository
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.playback.mediasource.PlaylistMediaSource
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val browseTree: BrowseTree by inject()
    private val playlistMediaSource: PlaylistMediaSource by inject()
    private val favouriteSongsRepository: FavouriteSongsRepository by inject()
    private val preferences: SharedPreferences by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) { // activity onCreate called twice
            playlistMediaSource.load(this)
            favouriteSongsRepository.browseTree = browseTree
            browseTree.load()
            val isDarkTheme = preferences.getBoolean("dark_theme", false)
            ViewUtils.changeDarkTheme(isDarkTheme)
        }
        setContentView(R.layout.activity_main)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import com.jadebyte.jadeplayer.main.playback.mediasource.PlaylistMediaSource
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val browseTree: BrowseTree by inject()
    private val playlistMediaSource: PlaylistMediaSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistMediaSource.load(this)
        browseTree.load()
        setContentView(R.layout.activity_main)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.playback.mediasource.BasicMediaStoreSource
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val mediaSource: BasicMediaStoreSource by inject()
    private val browseTree: BrowseTree by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaSource.load()
        browseTree.load(this)
        setContentView(R.layout.activity_main)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

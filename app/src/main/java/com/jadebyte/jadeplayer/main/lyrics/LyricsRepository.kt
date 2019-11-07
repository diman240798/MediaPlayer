// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.lyrics

import com.jadebyte.jadeplayer.main.playback.Lyrics

class LyricsRepository {
    private val lyricsMap : MutableMap<String, Lyrics?> = HashMap()

    fun getLyrics(id: String): Lyrics? {
        return lyricsMap[id]
    }

    fun set(id: String, lyrics: Lyrics?) {
        lyricsMap.put(id, lyrics)
    }

}

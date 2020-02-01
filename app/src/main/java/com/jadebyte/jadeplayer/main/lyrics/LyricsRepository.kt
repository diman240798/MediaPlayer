package com.jadebyte.jadeplayer.main.lyrics

class LyricsRepository(val lyricsDao: LyricsDao) {
    fun getLyrics(id: String) : Lyrics {
        return lyricsDao.getById(id)
    }

    fun save(lyrics: Lyrics) {
        lyricsDao.upsert(lyrics)
    }

}

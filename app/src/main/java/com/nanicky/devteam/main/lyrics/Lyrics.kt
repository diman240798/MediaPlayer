package com.nanicky.devteam.main.lyrics

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Lyrics(
    @PrimaryKey
    var id: String,
    val artist: String,
    val song: String,
    val lyrics: String?,
    val lyricsSource: String?) {}
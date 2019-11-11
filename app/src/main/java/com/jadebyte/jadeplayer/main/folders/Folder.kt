package com.jadebyte.jadeplayer.main.folders

import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.songs.Song

class Folder(
    override val id: String,
    val name: String,
    val path: String,
    songs: List<Song>
) : Model() {

}

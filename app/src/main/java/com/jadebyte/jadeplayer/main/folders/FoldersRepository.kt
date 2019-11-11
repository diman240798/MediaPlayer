package com.jadebyte.jadeplayer.main.folders

import androidx.lifecycle.MutableLiveData
import com.jadebyte.jadeplayer.main.songs.Song
import java.io.File

class FoldersRepository {
    val items: MutableLiveData<List<Folder>> = MutableLiveData<List<Folder>>()

    init {
        val songs = getSongs()
        val folders = songs.groupBy { it.path }.map { (path, songs) -> Folder(path, File(path).name, path, songs) }
        items.value = folders
    }

    private fun getSongs(): List<Song> {
        return listOf<Song>(Song())
    }

}

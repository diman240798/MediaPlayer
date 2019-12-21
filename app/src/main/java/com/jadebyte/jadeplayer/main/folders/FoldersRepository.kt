package com.jadebyte.jadeplayer.main.folders

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jadebyte.jadeplayer.main.songs.*
import java.io.File

class FoldersRepository {
    val items: MutableLiveData<List<Folder>> = MutableLiveData<List<Folder>>()

    fun loadData(application: Application) {
        val songs = getSongs(application)
        val folders = songs.groupBy { File(it.path).parentFile.canonicalPath } .map { (path, songs) -> Folder(path, File(path).name, path, songs) }
        items.value = folders
    }

    private fun getSongs(application: Application): List<Song> {
        val songsRepository = SongsRepository(application)
        return songsRepository.loadData(baseSongUri, baseSongsProjection, basicSongsSelection, basicSongsSelectionArgs, basicSongsOrder)
    }

}
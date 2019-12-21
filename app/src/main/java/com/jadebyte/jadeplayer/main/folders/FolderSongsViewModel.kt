package com.jadebyte.jadeplayer.main.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FolderSongsViewModel : ViewModel() {
    public val folder: LiveData<Folder> get() =  _folder
    private val _folder: MutableLiveData<Folder> = MutableLiveData<Folder>()

    fun setFolder(folder: Folder) {
        _folder.value = folder
    }
}

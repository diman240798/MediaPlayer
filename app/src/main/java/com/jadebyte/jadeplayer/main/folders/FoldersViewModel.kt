package com.jadebyte.jadeplayer.main.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class FoldersViewModel : ViewModel() {
    private val foldersRepository : FoldersRepository
    val items : LiveData<List<Folder>>


    init {
        foldersRepository = FoldersRepository()
        items = foldersRepository.items
    }
}
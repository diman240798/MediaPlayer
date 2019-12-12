package com.jadebyte.jadeplayer.main.folders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class FoldersViewModel(application: Application) : AndroidViewModel(application) {
    private val foldersRepository : FoldersRepository = FoldersRepository()
    val items : LiveData<List<Folder>>


    init {
        foldersRepository.loadData(application)
        items = foldersRepository.items
    }
}
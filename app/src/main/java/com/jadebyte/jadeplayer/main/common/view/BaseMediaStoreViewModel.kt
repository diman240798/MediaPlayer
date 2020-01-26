// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.view

import android.app.Application
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jadebyte.jadeplayer.main.common.callbacks.ModelSupplier
import com.jadebyte.jadeplayer.main.common.data.MediaStoreRepository
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.launch

/**
 *  Created by Wilberforce on 19/04/2019 at 16:45.
 *  Base class for all ViewHolders that relies on [MediaStore] for data
 *
 *  All Activities or Fragments using this ViewModel must call the [init] function to initialize
 *  fetching of data from [MediaStore] and watching it for subsequent changes.
 *
 */
abstract class BaseMediaStoreViewModel<T : Model>(
    application: Application, private val browseTree: BrowseTree,
    val itemFactory: ModelSupplier<T>
) : AndroidViewModel(application) {

    private var sourceConst: String? = null
    protected val data = MutableLiveData<List<T>>()
    val items: LiveData<List<T>> get() = data

    /**/

    /**
     *  Fetch data from the [MediaStore] and watch it for changes to the data at [uri]]
     */
    @CallSuper
    open fun init(sourceConst: String?) {
        this.sourceConst = sourceConst
        sourceConst?.let {
            loadData(sourceConst)
        }
    }


    private fun loadData(sourceConst: String) {
        viewModelScope.launch {
            /*val result = withContext(Dispatchers.IO) {
                repository.loadData(uri, projection, selection, selectionArgs, sortOrder)
            }*/
            val result = browseTree[sourceConst]?.map {
                itemFactory.get(it)
            }
            result?.let {
                deliverResult(result)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }


    // Give child classes the opportunity to intercept and modify result
    open fun deliverResult(items: List<T>) {
        if (data.value != items) data.value = items
    }

    open fun overrideCurrentItems(items: List<T>) {
        data.value = items
    }

    fun update() {
        init(sourceConst)
    }
}


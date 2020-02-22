package com.nanicky.devteam.main.common.view

import android.app.Application
import android.provider.MediaStore
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nanicky.devteam.main.common.callbacks.ModelSupplier
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import kotlinx.coroutines.launch

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


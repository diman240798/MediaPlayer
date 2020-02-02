package com.nanicky.devteam.main.common.data

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread

abstract class MediaStoreRepository<T>(application: Application) : BaseMediaStoreRepository(application) {

    @WorkerThread
    fun loadData(
        uri: Uri,
        projection: Array<String>? = null,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null
    ): List<T> {
        return loadData(uri, projection, selection, selectionArgs, sortOrder) { transform(it) }
    }

    /**
     * Converts [cursor] to [T].
     *
     * @param cursor the cursor to convert
     * @return the transformed [T]
     */
    abstract fun transform(cursor: Cursor): T
}
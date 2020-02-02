package com.nanicky.devteam.main.genres

import android.app.Application
import android.database.Cursor
import com.nanicky.devteam.main.common.data.MediaStoreRepository

class GenresRepository(application: Application) : MediaStoreRepository<Genre>(application) {

    override fun transform(cursor: Cursor) = Genre(cursor)
}
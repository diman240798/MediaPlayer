package com.nanicky.devteam.main.albums

import android.app.Application
import android.database.Cursor
import com.nanicky.devteam.main.common.data.MediaStoreRepository

class AlbumsRepository(application: Application) : MediaStoreRepository<Album>(application) {

    override fun transform(cursor: Cursor): Album = Album(cursor)


}
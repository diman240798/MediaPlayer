package com.nanicky.devteam.main.artists

import android.app.Application
import android.database.Cursor
import com.nanicky.devteam.main.common.data.MediaStoreRepository

class ArtistsRepository(application: Application) : MediaStoreRepository<Artist>(application) {
    override fun transform(cursor: Cursor): Artist = Artist(cursor)
}
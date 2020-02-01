package com.jadebyte.jadeplayer.main.favourite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jadebyte.jadeplayer.main.db.AppRoomDatabase
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongsRepository

class FavouriteSongsViewModel(application: Application) : AndroidViewModel(application) {
    var favouriteSongsRepository: FavouriteSongsRepository

    init {
        val database = AppRoomDatabase.getDatabase(application)
        favouriteSongsRepository = FavouriteSongsRepository(database.favouriteSongsDao())
    }
}
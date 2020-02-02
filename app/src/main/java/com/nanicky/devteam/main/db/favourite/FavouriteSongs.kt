package com.nanicky.devteam.main.db.favourite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nanicky.devteam.main.common.data.Model

@Entity(tableName = "favourite_songs")
data class FavouriteSongs(
    @PrimaryKey
    override val id: Int,
    val ids: MutableList<String>
) : Model()
package com.jadebyte.jadeplayer.main.db.favourite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jadebyte.jadeplayer.main.common.data.Model

@Entity(tableName = "favourite_songs")
data class FavouriteSongs(
    @PrimaryKey
    override val id: Int,
    val ids: MutableList<String>
) : Model()
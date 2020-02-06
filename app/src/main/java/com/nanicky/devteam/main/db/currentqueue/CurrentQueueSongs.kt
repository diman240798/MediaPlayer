package com.nanicky.devteam.main.db.currentqueue

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nanicky.devteam.main.common.data.Model

@Entity(tableName = "current_queue_songs")
data class CurrentQueueSongs(
    @PrimaryKey
    override val id: Int,
    var items: MutableList<String>
) : Model()
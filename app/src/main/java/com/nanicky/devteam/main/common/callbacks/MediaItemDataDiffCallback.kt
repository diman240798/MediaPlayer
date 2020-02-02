package com.nanicky.devteam.main.common.callbacks

import com.nanicky.devteam.main.playback.MediaItemData


class MediaItemDataDiffCallback(private val oldList: List<MediaItemData>, private val newList: List<MediaItemData>) :
    BaseDiffCallback<MediaItemData>(oldList, newList) {

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        newList[newItemPosition].areContentsTheSame(oldList[oldItemPosition])
}
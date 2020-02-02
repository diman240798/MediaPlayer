package com.nanicky.devteam.main.common.dragSwipe

import androidx.recyclerview.widget.ItemTouchHelper

/**
 * Interface to notify an item ViewHolder of relevant callbacks from [android.support.v7.widget.helper.ItemTouchHelper.Callback].
 */
interface ItemTouchHelperViewHolder {
    /**
     * Called when the [ItemTouchHelper] first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    fun onItemSelected()

    /**
     * Called when the [ItemTouchHelper] has completed the move or swipe, and the active item
     * state should be cleared.
     */
    fun onItemClear()
}

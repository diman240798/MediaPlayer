package com.nanicky.devteam.main.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.collection.SparseArrayCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.callbacks.BaseDiffCallback
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.data.Model

class BaseAdapter<T : Model>(
    items: List<T>,
    private val context: Context,
    private val layoutId: Int,
    private val variableId: Int,
    private val itemClickListener: OnItemClickListener? = null,
    private val animSet: Set<Int>? = setOf(R.anim.up_from_bottom, R.anim.down_from_top),
    private val longClick: Boolean = false,
    private var variables: SparseArrayCompat<Any>? = null
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private var items: MutableList<T>

    init {
        this.items = items.toMutableList()
    }

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, parent, false)
        variables?.let {
            for (i in 0 until it.size()) {
                itemBinding.setVariable(it.keyAt(i), it.valueAt(i))
            }
        }
        return BaseViewHolder(itemBinding, variableId, itemClickListener, longClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
        holder.bind(items[position])

    override fun onBindViewHolder(
        holder: BaseViewHolder<T>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) animateItem(position, holder)
    }

    fun updateItems(
        newItems: List<T>,
        diffCallback: BaseDiffCallback<T> = BaseDiffCallback(this.items, newItems)
    ) {
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)
        diffResult.dispatchUpdatesTo(this)
        //get the current items
        val currentSize = this.items.size
        //remove the current items
        this.items.clear()
        //add all the new items
        this.items.addAll(newItems)
        //tell the recycler view that all the old items are gone
        notifyItemRangeRemoved(0, currentSize)
        //tell the recycler view how many new items we added
        notifyItemRangeInserted(0, newItems.size)
        this.items = newItems.toMutableList()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    private fun animateItem(position: Int, holder: RecyclerView.ViewHolder) {
        if (animSet == null || animSet.isEmpty()) return

        val animation = AnimationUtils.loadAnimation(
            context,
            if (animSet.size == 1) {
                animSet.first()
            } else {
                if (position > lastPosition)
                    animSet.first()
                else
                    animSet.elementAt(1)
            }
        )
        holder.itemView.startAnimation(animation)
        lastPosition = position
    }

    override fun getItemViewType(position: Int): Int = layoutId
}

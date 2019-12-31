// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.albums


import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AlbumsFragment : BasePlayerFragment<Album>() {
    override val viewModel : AlbumsViewModel by sharedViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun layoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(activity).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.SPACE_EVENLY
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(Constants.ALBUMS_ROOT)
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            AlbumsFragmentDirections.actionAlbumsFragmentToAlbumSongsFragment(items[position], transitionName)
        findNavController().navigate(action, extras)

    }

    override fun onItemLongClick(position: Int) {
        val action =
            AlbumsFragmentDirections.actionAlbumsFragmentToAlbumsMenuBottomSheetDialogFragment(album = items[position])
        findNavController().navigate(action)
    }

    override var itemLayoutId: Int = R.layout.item_album
    override var viewModelVariableId: Int = BR.album
    override var navigationFragmentId: Int = R.id.action_albumsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = R.plurals.numberOfAlbums
    override var titleRes: Int = R.string.albums
    override var adapterItemAnimSet = albumItemAnimSet
    override var longClickItems: Boolean = true

}

val albumItemAnimSet = setOf(R.anim.fast_fade_in)



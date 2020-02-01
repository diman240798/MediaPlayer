// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SongsFragment : BasePlayerFragment<Song>() {
    private val songsMenuBottomDialogVM: SongsMenuBottomSheetDialogFragmentViewModel by sharedViewModel()
    override val viewModel: SongsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(Constants.SONGS_ROOT)
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        playbackViewModel.playAll(items[position].id.toString())
    }

    override fun onOverflowMenuClick(position: Int) {
        // update vm
        val song = items[position]
        songsMenuBottomDialogVM.setSong(song)
        // change fragment
        val action =
            SongsFragmentDirections.actionSongsFragmentToSongsMenuBottomSheetDialogFragment()
        findNavController().navigate(action)
    }

    override var itemLayoutId: Int = R.layout.item_song
    override var viewModelVariableId: Int = BR.song
    override var navigationFragmentId: Int = R.id.action_songsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = R.plurals.numberOfSongs
    override var titleRes: Int = R.string.songs

}

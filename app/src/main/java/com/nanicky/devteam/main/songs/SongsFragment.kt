package com.nanicky.devteam.main.songs


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.view.BasePlayerFragment
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
        playbackViewModel.playAll(items[position].id)
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

package com.jadebyte.jadeplayer.main.favourite

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.view.BasePlayerFragment
import com.jadebyte.jadeplayer.main.songs.Song
import com.jadebyte.jadeplayer.main.songs.SongsFragmentDirections
import com.jadebyte.jadeplayer.main.songs.SongsMenuBottomSheetDialogFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FavouriteSongsFragment : BasePlayerFragment<Song>() {
    private val songsMenuBottomDialogVM: SongsMenuBottomSheetDialogFragmentViewModel by sharedViewModel()
    override val viewModel: FavouriteSongsViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(Constants.FAVOURITES_ROOT)
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        playbackViewModel.playFavourites(items[position].id)
    }

    override fun onOverflowMenuClick(position: Int) {
        TODO()
        /*// update vm
        val song = items[position]
        songsMenuBottomDialogVM.setSong(song)
        // change fragment
        val action = FavouriteSongsFragmentDirections.actionFavouriteSongsFragmentToNavigationDialogFragment()
        findNavController().navigate(action)*/
    }

    override var itemLayoutId: Int = R.layout.item_song
    override var viewModelVariableId: Int = BR.song
    override var navigationFragmentId: Int = R.id.action_favouriteSongsFragment_to_navigationDialogFragment
    override var numberOfDataRes: Int = R.plurals.numberOfSongs
    override var titleRes: Int = R.string.favourites

}

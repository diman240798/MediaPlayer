// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.Utils
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet
import com.jadebyte.jadeplayer.main.db.favourite.FavouriteSongsRepository
import com.jadebyte.jadeplayer.main.favourite.FavouriteSongsViewModel
import com.jadebyte.jadeplayer.main.playback.PlaybackViewModel
import com.jadebyte.jadeplayer.main.web.WebFragmentViewModel
import kotlinx.android.synthetic.main.fragment_playback_songs_menu_bottom_sheet_dialog.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PlaybackSongsMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    private val webVM: WebFragmentViewModel by sharedViewModel()
    private val playbackViewModel: PlaybackViewModel by sharedViewModel()
    private val favouriteSongsRepository: FavouriteSongsRepository by inject()

    private val viewModel: SongsMenuBottomSheetDialogFragmentViewModel by sharedViewModel()
    @IdRes
    private var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popUpTo = arguments!!.getInt("popUpTo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_playback_songs_menu_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViews()
    }

    fun updateViews() {
        updateFavourite()
    }

    private fun updateFavourite() {
        val currentId = viewModel.song.value?.id
        currentId?.let {
            val contains = favouriteSongsRepository.containsId(currentId)
            val heartImageId =
                if (contains) R.drawable.ic_heart_filled
                else R.drawable.ic_heart
            val heartImageDrawable = resources.getDrawable(heartImageId)
            heartImageDrawable.setBounds(0, 0, 40, 40)
            favourite.setCompoundDrawables(null, null, heartImageDrawable, null)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchAuthor -> searchAuthorWeb()
            R.id.share -> shareTrack()
            R.id.playNext -> playNextTrack()
            R.id.favourite -> favouriteTrack()
            R.id.addToPlayList -> addTrackToPlayList()
            R.id.delete -> deleteTrackFromQueue()
        }
    }

    private fun searchAuthorWeb() {
        // update vm
        webVM.setSearchString(this.viewModel.song.value!!)
        // change fragment
        val action =
            PlaybackSongsMenuBottomSheetDialogFragmentDirections.actionPlaybackSongsMenuBottomSheetDialogFragmentToWebFragment()
        findNavController().navigate(action)
    }

    private fun deleteTrackFromQueue() {
        playbackViewModel.removeFromQueue()
    }

    private fun addTrackToPlayList() {
        /*val selection = "$basicSongsSelection AND ${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(basicSongsSelectionArg, viewModel.song.value!!.id.toString())
        val action = SongsMenuBottomSheetDialogFragmentDirections
            .actionSongsMenuBottomSheetDialogFragmentToAddSongsToPlaylistsFragment(selectionArgs, selection)
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, false).build()
        findNavController().navigate(action, navOptions)*/
    }

    private fun favouriteTrack() {
        viewModel.song.value?.id?.let {
            favouriteSongsRepository.addRemove(it)
        }
        updateViews()
    }

    private fun shareTrack() {
        context?.also { context ->
            val song = viewModel.song.value
            song?.also {
                Utils.share(context, song.title, song.album.artist, "Share Song")
            }
        }
    }

    private fun playNextTrack() {

    }


}

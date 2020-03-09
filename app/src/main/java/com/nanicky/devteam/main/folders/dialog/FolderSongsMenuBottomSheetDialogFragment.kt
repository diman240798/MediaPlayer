// Copyright (c) 2020 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.nanicky.devteam.main.songs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.utils.Utils
import com.nanicky.devteam.main.common.view.BaseMenuBottomSheet
import com.nanicky.devteam.main.db.favourite.FavouriteSongsRepository
import com.nanicky.devteam.main.folders.FolderSongsViewModel
import com.nanicky.devteam.main.playback.PlaybackViewModel
import com.nanicky.devteam.main.web.WebFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folder_songs_menu_bottom_sheet_dialog.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FolderSongsMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    private val viewModel: FolderSongsViewModel by sharedViewModel()
    private val webVM: WebFragmentViewModel by sharedViewModel()
    private val playbackViewModel: PlaybackViewModel by sharedViewModel()
    private val favouriteSongsRepository: FavouriteSongsRepository by inject()

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
            R.layout.fragment_folder_songs_menu_bottom_sheet_dialog,
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
        val currentId = viewModel.song?.id
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
            R.id.favourite -> favouriteTrack()
            R.id.addToPlayList -> addTrackToPlayList()
        }
    }

    private fun searchAuthorWeb() {
        // update vm
        webVM.setSearchString(this.viewModel.song!!)
        // change fragment
        val action =
            FolderSongsMenuBottomSheetDialogFragmentDirections.actionFolderSongsMenuBottomSheetDialogFragmentToWebFragment()
        findNavController().navigate(action)
    }

    private fun addTrackToPlayList() {
        val action = FolderSongsMenuBottomSheetDialogFragmentDirections.actionFolderSongsMenuBottomSheetDialogFragmentToAddSongsToPlaylistsFragment(songId = viewModel.song!!.id)
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, false).build()
        findNavController().navigate(action, navOptions)
    }

    private fun favouriteTrack() {
        viewModel.song?.id?.let {
            favouriteSongsRepository.addRemove(it)
        }
        updateViews()
    }

    private fun shareTrack() {
        context?.also { context ->
            viewModel.song?.also { song ->
                Utils.share(context, song.title, song.album.artist, "Share Song")
            }
        }
    }

}

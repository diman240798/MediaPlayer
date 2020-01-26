// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet
import com.jadebyte.jadeplayer.main.playback.mediasource.basicSongsSelection
import com.jadebyte.jadeplayer.main.playback.mediasource.basicSongsSelectionArg
import com.jadebyte.jadeplayer.main.web.WebFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class SongsMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    private val viewModel: SongsMenuBottomSheetDialogFragmentViewModel by sharedViewModel()
    private val webVM: WebFragmentViewModel by sharedViewModel()

    @IdRes private var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popUpTo = arguments!!.getInt("popUpTo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.searchAuthor -> searchAuthorWeb()
            R.id.share -> shareTrack()
            R.id.playNext -> playNextTrack()
            R.id.favourite -> favouriteTrack()
            R.id.addToPlayList -> addTrackToPlayList()
            R.id.delete -> deleteTrack()
        }
    }

    private fun searchAuthorWeb() {
        // update vm
        webVM.setSearchString(this.viewModel.song.value!!)
        // change fragment
        val action = SongsMenuBottomSheetDialogFragmentDirections.actionSongsMenuBottomSheetDialogFragmentToWebFragment()
        findNavController().navigate(action)
    }

    private fun deleteTrack() {
        // TODO: Implement
    }

    private fun addTrackToPlayList() {
        val selection = "$basicSongsSelection AND ${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(basicSongsSelectionArg, viewModel.song.value!!.id.toString())
        val action = SongsMenuBottomSheetDialogFragmentDirections
            .actionSongsMenuBottomSheetDialogFragmentToAddSongsToPlaylistsFragment(selectionArgs, selection)
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, false).build()
        findNavController().navigate(action, navOptions)
    }

    private fun favouriteTrack() {
        // TODO: Implement
    }

    private fun shareTrack() {
        // TODO: Implement
    }

    private fun playNextTrack() {
        // TODO: Implement
    }


}

// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.songs


import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet
import com.jadebyte.jadeplayer.main.web.WebFragmentViewModel


class PlaybackSongsMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    private lateinit var viewModel: SongsMenuBottomSheetDialogFragmentViewModel
    @IdRes private var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run { ViewModelProviders.of(this)[SongsMenuBottomSheetDialogFragmentViewModel::class.java] }!!
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
        val viewModelWeb : WebFragmentViewModel = activity?.run { ViewModelProviders.of(this)[WebFragmentViewModel::class.java] }!!
        viewModelWeb.setSearchString(this.viewModel.song.value!!)
        val action = PlaybackSongsMenuBottomSheetDialogFragmentDirections.actionPlaybackSongsMenuBottomSheetDialogFragmentToWebFragment()
        findNavController().navigate(action)
    }

    private fun deleteTrack() {
        // TODO: Implement
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
        // TODO: Implement
    }

    private fun shareTrack() {
        // TODO: Implement
    }

    private fun playNextTrack() {
        // TODO: Implement
    }


}

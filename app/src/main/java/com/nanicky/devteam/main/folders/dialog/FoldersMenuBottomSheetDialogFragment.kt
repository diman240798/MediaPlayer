// Copyright (c) 2020 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.nanicky.devteam.main.folders.dialog


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
import com.nanicky.devteam.main.folders.FolderSongsViewModel
import com.nanicky.devteam.main.playback.PlaybackViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FoldersMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    private val viewModel: FolderSongsViewModel by sharedViewModel()
    @IdRes var popUpTo: Int = 0
    private val playBackViewModel: PlaybackViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        popUpTo = arguments!!.getInt("popUpTo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_albums_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.play -> play()
            R.id.playNext -> playNext()
            R.id.addToPlayList -> addToPlayList()
            R.id.share -> share()
        }
    }

    private fun play() {
        playBackViewModel.playFolder(viewModel.folder!!.path)
        findNavController().popBackStack()
    }

    private fun playNext() {
        findNavController().popBackStack()
    }

    private fun addToPlayList() {
        val action = FoldersMenuBottomSheetDialogFragmentDirections
            .actionFoldersMenuBottomSheetDialogFragmentToAddSongsToPlaylistsFragment(mediaRoot = viewModel.folder!!.path)
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, false).build()
        findNavController().navigate(action, navOptions)
    }

    private fun share() {
        val folder = viewModel.folder!!
        context?.also { Utils.share(it, folder.name, "folder", "Share Folder") }
    }
}

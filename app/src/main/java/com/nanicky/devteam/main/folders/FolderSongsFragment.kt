package com.nanicky.devteam.main.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.databinding.FragmentFolderSongsBinding
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFragment
import com.nanicky.devteam.main.playback.PlaybackViewModel
import com.nanicky.devteam.main.songs.Song
import kotlinx.android.synthetic.main.fragment_folder_songs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class FolderSongsFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {

    private val playbackViewModel: PlaybackViewModel by sharedViewModel()
    private val viewModel: FolderSongsViewModel by sharedViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFolderSongsBinding.inflate(inflater, container, false)
        binding.folderSongsVM = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.folder?.let { ViewCompat.setTransitionName(folderArt, it.id) }
        setupViews()
        observeViewModel()
        sectionBackButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewModel.init(viewModel.folder?.path)
        viewModel.items.observe(viewLifecycleOwner, Observer(this::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        (folderSongsRV.adapter as BaseAdapter<Song>).updateItems(items)
    }


    private fun setupViews() {
        val adapter = BaseAdapter(
            viewModel.items.value ?: Collections.emptyList(),
            activity!!,
            R.layout.item_song,
            BR.song,
            itemClickListener = this
        )
        folderSongsRV.adapter = adapter
        folderSongsRV.layoutManager = LinearLayoutManager(activity!!)
        moreOptions.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        /*when (v?.id) {
            R.id.moreOptions -> findNavController().navigate(
                *//*AlbumSongsFragmentDirections
                    .actionAlbumSongsFragmentToAlbumsMenuBottomSheetDialogFragment(album = album)*//*
            )
        }*/
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val song = viewModel.items.value!!.get(position)
        val folder = viewModel.folder
        playbackViewModel.playFolder(folder!!.path, song.id)
    }
}

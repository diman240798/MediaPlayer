package com.jadebyte.jadeplayer.main.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.jadebyte.jadeplayer.BR
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentFolderSongsBinding
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.playback.PlaybackViewModel
import com.jadebyte.jadeplayer.main.songs.Song
import kotlinx.android.synthetic.main.fragment_folder_songs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Collections

class FolderSongsFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var viewModel: FolderSongsViewModel
    private val playbackViewModel: PlaybackViewModel by sharedViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        viewModel = activity?.run {ViewModelProviders.of(this)[FolderSongsViewModel::class.java] }!!

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFolderSongsBinding.inflate(inflater, container, false)
        binding.folderSongsVM = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.folder.value?.let { ViewCompat.setTransitionName(folderArt, it.id) }
        setupViews()
        observeViewModel()
        sectionBackButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewModel.folder.observe(viewLifecycleOwner, Observer(this::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(folder: Folder) {
        folderSongsDuration.text = getSongsTotalTime(folder.songs)
        (folderSongsRV.adapter as BaseAdapter<Song>).updateItems(folder.songs)
    }

    private fun setupViews() {
        val adapter = BaseAdapter(viewModel.folder.value?.songs ?: Collections.emptyList(), activity!!, R.layout.item_song, BR.song, itemClickListener = this)
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
        val folder = viewModel.folder.value
        playbackViewModel.playFolder(folder!!.path, folder.songs[position].id.toString())
    }
}

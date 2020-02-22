package com.nanicky.devteam.main.playlist


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
import com.nanicky.devteam.common.urlEncoded
import com.nanicky.devteam.databinding.FragmentPlaylistSongsBinding
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFragment
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.playback.PlaybackViewModel
import com.nanicky.devteam.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playlist_songs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PlaylistSongsFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentPlaylistSongsBinding
    private val songsViewModel: PlaylistSongsViewModel by sharedViewModel()
    private val playbackViewModel: PlaybackViewModel by sharedViewModel()
    private lateinit var playlist: Playlist
    private var items = emptyList<Song>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        playlist = arguments!!.getParcelable("playlist")!!
        songsViewModel.init(playlist.getUniqueKey())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistSongsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.playlist = playlist
        binding.lifecycleOwner = viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(playlistArt, arguments!!.getString("transitionName"))
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        songsViewModel.items.observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        this.items = items
        playlistSongsDuration.text = getSongsTotalTime(items)
        (playlistSongsRV.adapter as BaseAdapter<Song>).updateItems(items)
    }

    private fun setupViews() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_model_song, BR.song, itemClickListener = this)
        playlistSongsRV.adapter = adapter
        playlistSongsRV.layoutManager = LinearLayoutManager(activity!!)
        sectionBackButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.moreOptions -> findNavController().navigate(
                PlaylistSongsFragmentDirections
                    .actionPlaylistSongsFragmentToPlaylistMenuBottomSheetDialogFragment(playlist = playlist)
            )
        }
    }


    override fun onItemClick(position: Int, sharableView: View?) {
        val playListId = playlist.getUniqueKey()
        playbackViewModel.playPlaylist(playListId, items[position].id)
    }


}

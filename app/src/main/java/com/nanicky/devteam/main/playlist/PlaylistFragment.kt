package com.nanicky.devteam.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFragment
import com.nanicky.devteam.main.db.playlist.PlaylistDb
import kotlinx.android.synthetic.main.fragment_playlist.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PlaylistFragment : BaseFragment(), OnItemClickListener, View.OnClickListener {

    private var items: List<PlaylistDb> = emptyList()
    private val viewModel: PlaylistFragmentViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        viewModel.init(Constants.PLAYLISTS_ROOT)
        viewModel.items.observe(viewLifecycleOwner, Observer {
            this.items = it
            (playlistRV.adapter as BaseAdapter<PlaylistDb>).updateItems(it)
            updateViews()
        })
    }

    private fun updateViews() {
        if (items.isEmpty()) {
            playlistsGroup.visibility = View.GONE
            noPlaylistGroup.visibility = View.VISIBLE
        } else {
            noPlaylistGroup.visibility = View.GONE
            playlistsGroup.visibility = View.VISIBLE
            playlistsNum.text = resources.getQuantityString(R.plurals.numberOfPlaylists, items.count(), items.count())
        }
    }

    private fun setupViews() {
        playlistRV.adapter = BaseAdapter(
            items, activity!!, R.layout.item_playlist, BR.playlist, this, longClick = true
        )
        val layoutManager = LinearLayoutManager(activity)
        playlistRV.layoutManager = layoutManager

        navigationIcon.setOnClickListener(this)
        addPlayListIcon.setOnClickListener(this)
        addPlayList.setOnClickListener(this)
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistSongsFragment(transitionName, items[position])
        findNavController().navigate(action, extras)
    }

    override fun onItemLongClick(position: Int) {
        super.onItemLongClick(position)
        val action =
            PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistMenuBottomSheetDialogFragment(playlist = items[position])
        findNavController().navigate(action)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.navigationIcon -> findNavController().navigate(R.id.action_playlistFragment_to_navigationDialogFragment)
            R.id.addPlayList, R.id.addPlayListIcon -> findNavController().navigate(
                R.id.action_playlistFragment_to_writePlaylistDialogFragment
            )
        }
    }
}

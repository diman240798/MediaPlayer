package com.nanicky.devteam.main.playlist


import android.animation.AnimatorSet
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.common.crossFadeWidth
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.event.Event
import com.nanicky.devteam.main.common.utils.Utils
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFullscreenDialogFragment
import com.nanicky.devteam.main.db.playlist.PlaylistDb
import kotlinx.android.synthetic.main.fragment_add_songs_to_playlists.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddSongsToPlaylistsFragment : BaseFullscreenDialogFragment(), OnItemClickListener, View.OnClickListener {
    private var playlists = emptyList<PlaylistDb>()
    private val viewModel: AddSongsToPlaylistsViewModel by sharedViewModel()
    private var crossFadeAnimatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val strUri = arguments!!.getString("songsUri")
        var uri: Uri? = null; if (strUri != null) uri = Uri.parse(strUri)
        val selection = arguments!!.getString("songsSelection")
        val selectionArgs = arguments!!.getStringArray("songsSelectionArgs")
        viewModel.init(uri?.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_songs_to_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        doneButton.setOnClickListener { addToPlayList() }
    }

    private fun observeViewModel() {
        viewModel.mediatorItems.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(data: Any) {
        crossFadeAnimatorSet?.cancel()
        if (data is Event<*>) {
            data.getContentIfNotHandled()?.let {
                it as InsertionResult
                crossFadeAnimatorSet = doneButton.crossFadeWidth(progressBar, 600, visibility = View.INVISIBLE)
                if (it.message != null) Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                if (it.success == null || it.success) {
                    if (it.success == true) Utils.vibrateAfterAction(activity)
                    findNavController().popBackStack()
                }
            }
        } else {
            this.playlists = data as List<PlaylistDb>
            (playlistRV.adapter as BaseAdapter<PlaylistDb>).updateItems(playlists)
            updateSelectedCount()
            content.crossFadeWidth(largeProgressBar)
        }
    }

    private fun updateSelectedCount() {
        val selectedPlaylists = playlists.filter { it.selected }
        val count = selectedPlaylists.count()
        if (count == 0) {
            crossFadeAnimatorSet?.cancel()
            crossFadeAnimatorSet = unselectedButtons.crossFadeWidth(doneButton, 600, visibility = View.INVISIBLE)
        } else {
            if (crossFadeAnimatorSet == null) {
                crossFadeAnimatorSet = doneButton.crossFadeWidth(unselectedButtons)
            } else if ((crossFadeAnimatorSet!!.isRunning) || doneButton.visibility != View.VISIBLE) {
                crossFadeAnimatorSet!!.cancel()
                crossFadeAnimatorSet = doneButton.crossFadeWidth(unselectedButtons, 600)
            }
        }
        dataNum.setText(
            resources.getQuantityString(
                R.plurals.numberOfPlaylistsSelected,
                count,
                count
            )
        )
    }

    private fun setupView() {
        val variables = SparseArrayCompat<Any>(1)
        variables.put(BR.selectable, true)
        val adapter = BaseAdapter(
            playlists, activity!!, R.layout.item_playlist, BR.playlist, this, variables = variables)
        playlistRV.adapter = adapter
        playlistRV.layoutManager = LinearLayoutManager(activity)
        closeButton.setOnClickListener(this)
        addPlayListIcon.setOnClickListener(this)
    }

    private fun addToPlayList() {
        crossFadeAnimatorSet?.cancel()
        progressBar.crossFadeWidth(doneButton, 600, visibility = View.VISIBLE)
        viewModel.addToPlaylist()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.closeButton -> dismissAllowingStateLoss()
            R.id.addPlayListIcon -> findNavController().navigate(AddSongsToPlaylistsFragmentDirections.actionAddSongsToPlaylistsFragmentToWritePlaylistDialogFragment())
        }
    }

    /**
     * DiffUtil should handle the changes but there's an issue in [AddSongsToPlaylistsViewModel.reverseSelection]
     * function that's preventing DiffUtil from detecting changes.
     *
     * The issue is that the data value in the [AddSongsToPlaylistsViewModel] and [playlists] point to
     * the same object in memory so any item change in the values are reflected across. So by the
     * time observers are notified, the current [playlists] and incoming items are already the same
     * hence DiffUtil can't detect changes
     */
    @Suppress("UNCHECKED_CAST")
    override fun onItemClick(position: Int, sharableView: View?) {
        if (viewModel.reverseSelection(position)) {
            (playlistRV.adapter as BaseAdapter<Playlist>).notifyItemChanged(position, 0)
            updateSelectedCount()
        }
    }
}

package com.nanicky.devteam.main.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.common.crossFadeWidth
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.utils.Utils
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFullscreenDialogFragment
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.songs.Song
import kotlinx.android.synthetic.main.fragment_playlist_songs_editor_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PlaylistSongsEditorDialogFragment : BaseFullscreenDialogFragment(), OnItemClickListener {

    var items = emptyList<Song>()
    val viewModel: PlaylistSongsEditorViewModel by sharedViewModel()
    private lateinit var playlist: Playlist


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = arguments!!.getParcelable("playlist")!!
        viewModel.init(playlist.getUniqueKey())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_playlist_songs_editor_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        doneButton.setOnClickListener { editPlaylist() }
    }

    private fun editPlaylist() {
        val animatorSet = progressBar.crossFadeWidth(doneButton, 500)
        viewModel.playlistValue.observe(viewLifecycleOwner, Observer { event ->

            event.getContentIfNotHandled()?.let {
                if (it) {
                    // Updating playlist was successful
                    Utils.vibrateAfterAction(activity)
                    findNavController().popBackStack()
                } else {
                    // Updating playlist wasn't successful;
                    if (animatorSet.isRunning) animatorSet.cancel()
                    doneButton.crossFadeWidth(progressBar, 500, visibility = View.INVISIBLE)
                    Toast.makeText(activity, R.string.sth_went_wrong, Toast.LENGTH_SHORT).show()
                }
            }

        })
        viewModel.updatePlaylist(playlist)

    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Song>) {
        this.items = items
        (songsRV.adapter as BaseAdapter<Song>).updateItems(items)
        updateSelectedCount()
        content.crossFadeWidth(largeProgressBar)
    }

    private fun updateSelectedCount() {
        val selectedSongs = items.filter { it.selected }
        dataNum.setText(
            resources.getQuantityString(
                R.plurals.numberOfSongsSelected,
                selectedSongs.count(),
                selectedSongs.count()
            )
        )
    }

    private fun setupView() {
        val variables = SparseArrayCompat<Any>(1)
        variables.put(BR.selectable, true)
        val adapter =
            BaseAdapter(items, activity!!, R.layout.item_song, BR.song, this, variables = variables)
        songsRV.adapter = adapter
        songsRV.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * DiffUtil should handle the changes but there's an issue in [PlaylistSongsEditorViewModel.reverseSelection]
     * function that's preventing DiffUtil from detecting changes.
     *
     * The issue is that the data value in the [PlaylistSongsEditorViewModel] and [items] point to
     * the same object in memory so any item change in the values are reflected across. So by the
     * time observers are notified, the current [items] and incoming items are already the same
     * hence DiffUtil can't detect changes
     */
    @Suppress("UNCHECKED_CAST")
    override fun onItemClick(position: Int, sharableView: View?) {
        if (viewModel.reverseSelection(position)) {
            (songsRV.adapter as BaseAdapter<Song>).notifyItemChanged(position, 0)
            updateSelectedCount()
        }
    }

}

package com.jadebyte.jadeplayer.main.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_playlist_songs_editor_dialog.*


class FoldersFragment : Fragment(), OnItemClickListener {
    private lateinit var viewModel: FoldersViewModel

    var items = emptyList<Folder>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[FoldersViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
    }

    private fun setupView() {
        val variables = SparseArrayCompat<Any>(1)
        variables.put(BR.selectable, true)
        val adapter =
            BaseAdapter(items, activity!!, R.layout.item_song, BR.song, this, variables = variables)
        songsRV.adapter = adapter
        songsRV.layoutManager = LinearLayoutManager(activity)
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner, Observer {items = it})
    }

    override fun onItemClick(position: Int, sharableView: View?) {

    }

}

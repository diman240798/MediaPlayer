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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.callbacks.OnItemClickListener
import com.jadebyte.jadeplayer.main.common.view.BaseAdapter
import com.jadebyte.jadeplayer.main.songs.SongsViewModel
import kotlinx.android.synthetic.main.fragment_folders.*


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
        val adapter = BaseAdapter(items, activity!!, R.layout.item_folder, BR.folder, this, variables = variables)
        foldersRecyclerView.adapter = adapter
        foldersRecyclerView.layoutManager = LinearLayoutManager(activity)
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_foldersFragment_to_navigationDialogFragment
            )
        )
        dataNum.text = resources.getQuantityString(R.plurals.numberOfFolders, items.count(), items.count())
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Folder>) {
        this.items = items
        (foldersRecyclerView.adapter as BaseAdapter<Folder>).updateItems(items)
        dataNum.text = resources.getQuantityString(R.plurals.numberOfFolders, items.count(), items.count())
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val folder: Folder = items[position]
        val songsViewModel = activity?.run {ViewModelProviders.of(this)[SongsViewModel::class.java] }!!
        songsViewModel.currentItems = folder.songs
        val action = FoldersFragmentDirections.actionFoldersFragmentToSongsFragment()
        findNavController().navigate(action)

    }

}

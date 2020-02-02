package com.nanicky.devteam.main.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.core.view.ViewCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.utils.ViewUtils
import com.nanicky.devteam.main.common.view.BaseAdapter
import kotlinx.android.synthetic.main.fragment_folders.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FoldersFragment : Fragment(), OnItemClickListener {
    private val folderSongsVM: FolderSongsViewModel by sharedViewModel()
    private val viewModel: FoldersViewModel by sharedViewModel()

    var items = emptyList<Folder>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        foldersRV.adapter = adapter
        foldersRV.layoutManager = LinearLayoutManager(activity)
        ViewUtils.postponeRecyclerViewEnterSharedElementTransitionForFragment(foldersRV, FoldersFragment@this)
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_foldersFragment_to_navigationDialogFragment
            )
        )
        dataNum.text = resources.getQuantityString(R.plurals.numberOfFolders, items.count(), items.count())
    }

    private fun observeViewModel() {
        viewModel.init(Constants.FOLDERS_ROOT)
        viewModel.items.observe(viewLifecycleOwner, Observer(::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(items: List<Folder>) {
        this.items = items
        (foldersRV.adapter as BaseAdapter<Folder>).updateItems(items)
        dataNum.text = resources.getQuantityString(R.plurals.numberOfFolders, items.count(), items.count())
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        // update vm
        val folder: Folder = items[position]
        folderSongsVM.folder = folder
        // change fragment
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action = FoldersFragmentDirections.actionFoldersFragmentToFolderSongsFragment()
        findNavController().navigate(action, extras)

    }

}

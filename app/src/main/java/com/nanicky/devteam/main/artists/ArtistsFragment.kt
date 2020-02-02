package com.nanicky.devteam.main.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_artists.*
import kotlinx.android.synthetic.main.fragment_explore.navigationIcon
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ArtistsFragment : BaseFragment(), OnItemClickListener {

    private var items: List<Artist> = emptyList()
    private val viewModel: ArtistsViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(Constants.ARTISTS_ROOT)
        setupRecyclerView()
        observeViewModel()
        navigationIcon.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                        R.id.action_artistsFragment_to_navigationDialogFragment
                )
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeViewModel() {
        if (items.isEmpty()) {
            viewModel.items.observe(viewLifecycleOwner, Observer {
                this.items = it
                (artistsRV.adapter as BaseAdapter<Artist>).updateItems(it)
            })
        } else {
            viewModel.overrideCurrentItems(items)

        }
    }

    private fun setupRecyclerView() {
        val adapter = BaseAdapter(items, activity!!, R.layout.item_artist, BR.artist, this)
        artistsRV.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        artistsRV.layoutManager = layoutManager
    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            ArtistsFragmentDirections.actionArtistsFragmentToArtistAlbumsFragment(items[position], transitionName)
        findNavController().navigate(action, extras)
    }


}

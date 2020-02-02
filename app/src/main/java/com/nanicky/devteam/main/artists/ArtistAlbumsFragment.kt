package com.nanicky.devteam.main.artists


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.common.px
import com.nanicky.devteam.databinding.FragmentArtistAlbumsBinding
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.common.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_artist_albums.*

class ArtistAlbumsFragment : BaseFragment(), OnItemClickListener {
    lateinit var artist: Artist
    lateinit var viewModel: ArtistAlbumsViewModel
    private var albums = emptyList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        artist = arguments!!.getParcelable("artist")!!
        viewModel = ViewModelProviders.of(this)[ArtistAlbumsViewModel::class.java]
        viewModel.init(artist.id.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentArtistAlbumsBinding.inflate(inflater, container, false)
        binding.artist = artist
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(sectionTitle, arguments!!.getString("transitionName"))
        setupRecyclerView()
        observeViewModel()
        sectionBackButton.setOnClickListener { findNavController().popBackStack() }
        navigationIcon.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_artistAlbumsFragment_to_navigationDialogFragment
            )
        )
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner, Observer(this::updateViews))
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateViews(albums: List<Album>) {
        if (albums.isEmpty()) {
            findNavController().popBackStack()
            return
        }
        this.albums = albums
        (artistAlbumsRV.adapter as BaseAdapter<Album>).updateItems(albums)
    }

    @SuppressLint("WrongConstant")
    private fun setupRecyclerView() {
        val adapter = BaseAdapter(
            albums, activity!!, R.layout.item_album, BR.album, itemClickListener = this,
            longClick = true
        )
        if (artist.albumsCount == 1L) {
            artistAlbumsRV.setPadding(20.px, 0, 0, 0)
        }
        artistAlbumsRV.adapter = adapter
        artistAlbumsRV.layoutManager = FlexboxLayoutManager(activity).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = if (artist.albumsCount > 1) JustifyContent.SPACE_EVENLY else JustifyContent.FLEX_START
        }


    }

    override fun onItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!
        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()
        val action =
            ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumSongsFragment(
                albums[position],
                transitionName
            )
        findNavController().navigate(action, extras)
    }

    override fun onItemLongClick(position: Int) {
        val action =
            ArtistAlbumsFragmentDirections.actionArtistAlbumsFragmentToAlbumsMenuBottomSheetDialogFragment(album = albums[position])
        findNavController().navigate(action)
        
    }
}

package com.nanicky.devteam.main.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nanicky.devteam.BR
import com.nanicky.devteam.R
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.albums.albumItemAnimSet
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.callbacks.OnItemClickListener
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.common.view.BaseAdapter
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.SongsMenuBottomSheetDialogFragmentViewModel
import kotlinx.android.synthetic.main.fragment_results.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val RESULT = "RESULT"


class ResultsFragment<T : Model> : Fragment(), OnItemClickListener {

    private val songsMenuBottomDialogVM: SongsMenuBottomSheetDialogFragmentViewModel by sharedViewModel()

    lateinit var result: Result
    var items = emptyList<Model>()
    private val viewModel: SearchViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        result = arguments!!.getParcelable(RESULT)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        when (result.type) {
            Type.Songs -> viewModel.songsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Albums -> viewModel.albumsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Artists -> viewModel.artistsResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Genres -> viewModel.genresResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
            Type.Playlists -> viewModel.playlistResults.observe(viewLifecycleOwner, Observer {
                items = it
                updateData()
            })
        }
    }

    private fun setupViews() {
        var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity!!)
        val adapter = when (result.type) {
            Type.Songs -> BaseAdapter(items, activity!!, R.layout.item_song, BR.song, this)
            Type.Albums -> {
                layoutManager = FlexboxLayoutManager(activity).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.SPACE_EVENLY
                }
                BaseAdapter(
                    items,
                    activity!!,
                    R.layout.item_album,
                    BR.album,
                    this,
                    albumItemAnimSet,
                    true
                )
            }
            Type.Artists -> BaseAdapter(items, activity!!, R.layout.item_artist, BR.artist, this)
            Type.Genres -> BaseAdapter(
                items,
                activity!!,
                R.layout.item_genre,
                BR.genre,
                this,
                longClick = true
            )
            Type.Playlists -> BaseAdapter(
                items, activity!!, R.layout.item_playlist, BR.playlist, this, longClick = true
            )
        }
        resultsRV.adapter = adapter
        resultsRV.layoutManager = layoutManager
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateData() = (resultsRV.adapter as BaseAdapter<T>).updateItems(items as List<T>)

    override fun onItemClick(position: Int, sharableView: View?) {
        when (result.type) {
            Type.Songs -> onSongItemClick(position)
            Type.Albums -> onAlbumItemClick(position, sharableView)
            Type.Artists -> onArtistItemClick(position, sharableView)
            Type.Genres -> onGenreItemClick(position, sharableView)
            Type.Playlists -> onPlaylistItemClick(position, sharableView)
        }
    }

    override fun onItemLongClick(position: Int) {
        when (result.type) {
            Type.Albums -> onAlbumItemLongClick(position)
            Type.Genres -> onGenreItemLongClick(position)
            Type.Playlists -> onPlaylistItemLongClick(position)
            else -> Unit
        }
    }

    override fun onOverflowMenuClick(position: Int) {
        when (result.type) {
            Type.Songs -> onSongItemOverflowMenuClick(position)
            else -> Unit
        }
    }

    private fun onPlaylistItemLongClick(position: Int) {
        val playlist = items[position] as Playlist

        val directions =
            SearchFragmentDirections.actionSearchFragmentToPlaylistMenuBottomSheetDialogFragment(
                playlist =  Playlist(
                    playlist.name,
                    playlist.selected,
                    playlist.songIds,
                    id = playlist.id
                )
            )
        viewModel.navigateFrmSearchFragment(SearchNavigation(directions))
    }

    private fun onAlbumItemLongClick(position: Int) {
        val directions =
            SearchFragmentDirections.actionSearchFragmentToAlbumsMenuBottomSheetDialogFragment(album = items[position] as Album)
        viewModel.navigateFrmSearchFragment(SearchNavigation(directions))
    }

    private fun onGenreItemLongClick(position: Int) {
        val directions =
            SearchFragmentDirections.actionSearchFragmentToGenresMenuBottomSheetDialogFragment(genre = items[position] as Genre)
        viewModel.navigateFrmSearchFragment(SearchNavigation(directions))
    }

    private fun onSongItemOverflowMenuClick(position: Int) {
        // update vm
        val song = items[position] as Song
        songsMenuBottomDialogVM.setSong(song)
        // change fragment
        val directions =
            SearchFragmentDirections.actionSearchFragmentToSongsMenuBottomSheetDialogFragment()
        this.viewModel.navigateFrmSearchFragment(SearchNavigation(directions))
    }


    private fun onPlaylistItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!

        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()

        val playlist = items[position] as Playlist

        val directions = SearchFragmentDirections.actionSearchFragmentToPlaylistSongsFragment(
            transitionName,
            Playlist(
                playlist.name,
                playlist.selected,
                playlist.songIds,
                id = playlist.id
            )

        )
        viewModel.navigateFrmSearchFragment(SearchNavigation(directions, extras))
    }

    private fun onGenreItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!

        val directions =
            SearchFragmentDirections.actionSearchFragmentToGenreSongsFragment(
                items[position] as Genre,
                transitionName
            )

        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()

        viewModel.navigateFrmSearchFragment(SearchNavigation(directions, extras))
    }

    private fun onArtistItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!

        val directions = SearchFragmentDirections
            .actionSearchFragmentToArtistAlbumsFragment(items[position] as Artist, transitionName)

        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()

        viewModel.navigateFrmSearchFragment(SearchNavigation(directions, extras))
    }

    private fun onAlbumItemClick(position: Int, sharableView: View?) {
        val transitionName = ViewCompat.getTransitionName(sharableView!!)!!

        val directions = SearchFragmentDirections
            .actionSearchFragmentToAlbumSongsFragment(items[position] as Album, transitionName)

        val extras = FragmentNavigator.Extras.Builder()
            .addSharedElement(sharableView, transitionName)
            .build()

        viewModel.navigateFrmSearchFragment(SearchNavigation(directions, extras))
    }

    private fun onSongItemClick(position: Int) {
        Toast.makeText(context, "Not Implemented", Toast.LENGTH_SHORT).show()
    }


    companion object {
        @JvmStatic
        fun <T : Model> newInstance(result: Result) =
            ResultsFragment<T>().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
    }
}

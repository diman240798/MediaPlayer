package com.nanicky.devteam.main.genres


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.utils.Utils
import com.nanicky.devteam.main.common.view.BaseMenuBottomSheet

class GenresMenuBottomSheetDialogFragment : BaseMenuBottomSheet() {

    lateinit var genre: Genre
    @IdRes var popUpTo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = arguments!!.getParcelable("genre")!!
        popUpTo = arguments!!.getInt("popUpTo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genres_menu_bottom_sheet_dialog, container, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.play -> play()
            R.id.playNext -> playNext()
            R.id.addToPlayList -> addToPlayList()
            R.id.share -> share()
        }
    }

    private fun play() {
        findNavController().popBackStack()
    }

    private fun playNext() {
        findNavController().popBackStack()
    }

    private fun addToPlayList() {
      // TODO: FIX
        /*  val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genre.id)
        val action = GenresMenuBottomSheetDialogFragmentDirections
            .actionGenresMenuBottomSheetDialogFragmentToAddSongsToPlaylistsFragment(songsUri = uri.toString())
        val navOptions = NavOptions.Builder().setPopUpTo(popUpTo, false).build()

        findNavController().navigate(action, navOptions)*/
    }

    private fun share() {
        context?.let { Utils.share(it, genre.name, "", "Share Genre") }
    }
}
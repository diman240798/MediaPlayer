// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.lyrics

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jadebyte.jadeplayer.R
import kotlinx.android.synthetic.main.fragment_lyrics.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val LYRICS_TEXT_PARAM = "lyricsText"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LyricsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LyricsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LyricsFragment : Fragment() {

    private var lyricsText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lyricsText = it.getString(LYRICS_TEXT_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lyrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lyrics.text = lyricsText

    }
}

// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.playback


import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.*
import com.jadebyte.jadeplayer.databinding.FragmentPlaybackBinding
import com.jadebyte.jadeplayer.main.common.callbacks.AnimatorListener
import com.jadebyte.jadeplayer.main.common.callbacks.OnSeekBarChangeListener
import com.jadebyte.jadeplayer.main.common.view.BaseFragment
import com.jadebyte.jadeplayer.main.lyrics.Lyrics
import kotlinx.android.synthetic.main.fragment_playback.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.Exception


class PlaybackFragment : BaseFragment(), View.OnClickListener {

    private var userTouchingSeekBar = false
    private val viewModel: PlaybackViewModel by sharedViewModel()
    private lateinit var rotationAnimSet: AnimatorSet
    private val handler = Handler()

    private var findingLyricsAnimatorSet: AnimatorSet? = null
    private var foundLyricsAnimatorSet: AnimatorSet? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPlaybackBinding.inflate(inflater, container, false).let {
        it.viewModel = viewModel
        it.lifecycleOwner = this
        return it.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewData()
    }


    private fun observeViewData() {
        viewModel.mediaPosition.observe(viewLifecycleOwner, Observer {
            if (!userTouchingSeekBar) playbackSeekBar.progress = it.toInt()
        })

        viewModel.currentItem.observe(viewLifecycleOwner, Observer {
            playbackSeekBar.max = it?.duration?.toInt() ?: 0
        })

        viewModel.playbackState.observe(viewLifecycleOwner, Observer { updateState(it) })
        viewModel.lyrics.observe(viewLifecycleOwner, Observer { showFoundLyrics(it) })
    }

    private fun setupView() {
        rotationAnimSet = AnimatorInflater.loadAnimator(activity, R.animator.album_art_rotation) as AnimatorSet
        rotationAnimSet.setTarget(albumArt)
        songTitle.children.forEach { (it as TextView).isSelected = true }
        sectionBackButton.setOnClickListener(this)
        lyricsButton.setOnClickListener(this)
        closeButton.setOnClickListener(this)
        moreOptions.setOnClickListener(this)
        playingTracks.setOnClickListener(this)
        lyricsText.setOnClickListener(this)
        playbackSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }


    private fun updateState(state: PlaybackStateCompat) {
        if (state.isPlaying) {
            if (playPauseButton.currentView != pauseButton) playPauseButton.showNext()
            if (!rotationAnimSet.isStarted) rotationAnimSet.start() else rotationAnimSet.resume()
        } else {
            rotationAnimSet.pause()
            if (playPauseButton.currentView != playButton) playPauseButton.showPrevious()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sectionBackButton -> findNavController().popBackStack()
            R.id.lyricsButton -> showFindingLyrics()
            R.id.closeButton -> closeLyrics()
            R.id.moreOptions -> showMenuBottomSheet()
            R.id.playingTracks -> showCurrentTracks()
            R.id.lyricsText -> goLyricsFragment()
        }
    }

    private fun goLyricsFragment() {
        val lyrics = viewModel.lyrics.value?.lyrics ?: "No Lyrics"
        val action = PlaybackFragmentDirections.actionPlaybackFragmentToLyricsFragment(lyrics)
        findNavController().navigate(action)
    }

    private fun showCurrentTracks() {
        if ((playingTracks.drawable as Animatable).isRunning) {
            return
        }

        val fragment = childFragmentManager.findFragmentByTag("CurrentSongsFragment")
        val animDrawable = AnimatedVectorDrawableCompat.create(
            activity!!,
            if (fragment != null) R.drawable.anim_close_to_playlist_current else R.drawable.anim_playlist_current_to_close
        )
        playingTracks.setImageDrawable(animDrawable)
        (playingTracks.drawable as Animatable).start()
        if (fragment == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.currentSongsContainer, CurrentSongsFragment(), "CurrentSongsFragment").commit()
        } else {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }

    }

    private fun showMenuBottomSheet() {
        val mediaItem = viewModel.currentItem.value ?: return
        val action =
            PlaybackFragmentDirections.actionPlaybackFragmentToSongsMenuBottomSheetDialogFragment(mediaItem.id.toLong())
        findNavController().navigate(action)
    }

    private fun closeLyrics() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            albumArt.fadeInSlideDown(translationY, slideDuration),
            lyricsButton.fadeInSlideDown(translationY, slideDuration),
            closeButton.fadeOutSlideDown(translationY, slideDuration),
            quoteImg.fadeOutSlideDown(translationY, slideDuration),
            lyricsText.fadeOutSlideDown(translationY, slideDuration),
            lyricsSource.fadeOutSlideDown(translationY, slideDuration)
        )
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                albumArtGroup.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                lyricsGroup.visibility = View.GONE
                animatorSet.removeAllListeners()
            }
        })

        animatorSet.start()
    }

    private fun showFindingLyrics() {
        /*if (hasLyrics()) {
            showFoundLyrics()
            return
        }*/

        findingLyricsAnimatorSet = AnimatorSet()
        findingLyricsAnimatorSet?.playTogether(
            albumArt.fadeOutSlideUp(translationY, slideDuration),
            lyricsButton.fadeOutSlideUp(translationY, slideDuration),
            progressBar.fadeInSlideUp(translationY, slideDuration),
            findingLyrics.fadeInSlideUp(translationY, slideDuration)
        )
        findingLyricsAnimatorSet?.interpolator = AccelerateDecelerateInterpolator()
        findingLyricsAnimatorSet?.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                try {
                loadingLyricsGroup.visibility = View.VISIBLE
                } catch (ignored: Exception) {}
            }

            override fun onAnimationEnd(animation: Animator?) {
                try {
                    albumArtGroup.visibility = View.GONE
                } catch (ignored: Exception) {}
                findingLyricsAnimatorSet?.removeAllListeners()
            }
        })

        val curItem = viewModel.currentItem.value
        if (curItem != null) {
            viewModel.getLyrics(curItem.id, curItem.subtitle, curItem.title)
            findingLyricsAnimatorSet?.start()
        }
    }

    private fun showFoundLyrics(lyrics: Lyrics?) {
        if (lyrics == null) return
        if (lyrics.id != viewModel.currentItem.value?.id) return

        lyricsText.text = lyrics.lyrics
        lyricsSource.text = lyrics.lyricsSource

        foundLyricsAnimatorSet = AnimatorSet()
        foundLyricsAnimatorSet?.playTogether(
            progressBar.fadeOutSlideUp(translationY, slideDuration), //else albumArt.fadeOutSlideUp(translationY, slideDuration),
            findingLyrics.fadeOutSlideUp(translationY, slideDuration), //else lyricsButton.fadeOutSlideUp(translationY, slideDuration),
            closeButton.fadeInSlideUp(translationY, slideDuration),
            quoteImg.fadeInSlideUp(translationY, slideDuration),
            lyricsText.fadeInSlideUp(translationY, slideDuration),
            lyricsSource.fadeInSlideUp(translationY, slideDuration)
        )

        foundLyricsAnimatorSet?.interpolator = AccelerateDecelerateInterpolator()
        foundLyricsAnimatorSet?.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                try {
                    albumArtGroup.visibility = View.GONE
                    lyricsGroup.visibility = View.VISIBLE
                } catch (ignored: Exception) {}
            }

            override fun onAnimationEnd(animation: Animator?) {
                try {
                    loadingLyricsGroup.visibility = View.GONE // else albumArtGroup.visibility = View.GONE
                } catch (ignored: Exception) {}
                foundLyricsAnimatorSet?.removeAllListeners()
            }
        })
        foundLyricsAnimatorSet?.start()
    }

    private val onSeekBarChangeListener = object : OnSeekBarChangeListener {

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            userTouchingSeekBar = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            viewModel.seek(seekBar?.progress?.toLong() ?: 0)
            // Let's delay the updating of the userTouchingSeekBar to ensure the seek has taken effect before
            // continuing to update the progress bar
            handler.postDelayed({ userTouchingSeekBar = false }, 200)
        }

    }

    private fun removeLyricsAnimators() {
        findingLyricsAnimatorSet?.removeAllListeners()
        findingLyricsAnimatorSet?.cancel()
        foundLyricsAnimatorSet?.removeAllListeners()
        foundLyricsAnimatorSet?.cancel()
    }

    override fun onDestroyView() {
        rotationAnimSet.cancel()
        removeLyricsAnimators()
        super.onDestroyView()
    }


}

private const val slideDuration = 600L
private val translationY = 110F.px

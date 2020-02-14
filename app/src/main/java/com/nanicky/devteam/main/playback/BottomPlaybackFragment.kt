package com.nanicky.devteam.main.playback


import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.nanicky.devteam.R
import com.nanicky.devteam.common.crossFadeWidth
import com.nanicky.devteam.databinding.FragmentBottomPlaybackBinding
import com.nanicky.devteam.main.MainFragmentDirections
import com.nanicky.devteam.main.common.view.BaseFragment
import com.nanicky.devteam.main.settings.ColorChangeSharedObject
import kotlinx.android.synthetic.main.fragment_bottom_playback.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BottomPlaybackFragment : BaseFragment() {

    private val viewModel: PlaybackViewModel by sharedViewModel()
    private val colorChangeSharedObject: ColorChangeSharedObject by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBottomPlaybackBinding.inflate(inflater, container, false).let {
        it.viewModel = viewModel
        it.lifecycleOwner = this
        return it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        var animatorSet: AnimatorSet? = null
        viewModel.playbackState.observe(viewLifecycleOwner, Observer {
            animatorSet?.cancel()
            animatorSet = if (it.isBuffering) {
                progressBar.crossFadeWidth(playButton, visibility = View.INVISIBLE)
            } else {
                playButton.crossFadeWidth(progressBar, visibility = View.INVISIBLE)
            }
        })
        val container = view.findViewById<ConstraintLayout>(R.id.container)
        colorChangeSharedObject.backgrColorBottomPlayBack.observe(viewLifecycleOwner, Observer { container.setBackgroundColor(resources.getColor(it)) })
    }

    private fun setupViews() {
        playbackSeekBar.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        clickableView.setOnClickListener {
            viewModel.currentItem.value?.let {
                val transitionName = ViewCompat.getTransitionName(sharableView)!!
                val extras = FragmentNavigator.Extras.Builder().addSharedElement(sharableView, transitionName).build()
                val action = MainFragmentDirections.actionMainFragmentToPlaybackFragment(transitionName)
                activity?.findNavController(R.id.mainNavHostFragment)?.navigate(action, extras)
            }
        }

        closeBottom.setOnClickListener {
            val parent = it.parent as ConstraintLayout
            parent.visibility = View.GONE
        }
    }


}

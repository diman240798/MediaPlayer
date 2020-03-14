package com.nanicky.devteam.main.equalizer

import android.content.SharedPreferences
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import kotlinx.android.synthetic.main.fragment_equalizer.*
import org.koin.android.ext.android.inject

class EqualizerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    internal val preferences: SharedPreferences by inject()
    internal val equalizerInitializer: EqualizerInitializer by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationIcon.setOnClickListener {
            findNavController().navigate(R.id.action_equalizerFragment_to_navigationDialogFragment)
        }

        val audioSessionId = equalizerInitializer.audioSessionId
        if (audioSessionId == null) {
            Toast.makeText(context, R.string.sessionNotSet, Toast.LENGTH_LONG).show()
            return
        }
        val mEqualizer = Equalizer(0, audioSessionId)
        mEqualizer.enabled = preferences.getBoolean(CONST_PREF_ENABLED, false)

        EqualizerUI.make(equalizerContainer, mEqualizer, spinner, preferences)

        eqSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences.edit().putBoolean(CONST_PREF_ENABLED, isChecked)
            mEqualizer.enabled = isChecked
        }

    }
}
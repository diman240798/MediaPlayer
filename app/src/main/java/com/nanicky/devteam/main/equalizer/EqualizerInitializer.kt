// Copyright (c) 2020 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.nanicky.devteam.main.equalizer

import android.content.SharedPreferences
import android.media.audiofx.Equalizer

class EqualizerInitializer(val preferences: SharedPreferences) {
    var audioSessionId: Int? = null

    fun initEqualizer() {
        val isEnabled = preferences.getBoolean(CONST_PREF_ENABLED, false)
        if (!isEnabled) return

        val mEqualizer = Equalizer(0, audioSessionId!!)
        mEqualizer.enabled = true

        val numberFrequencyBands: Short = mEqualizer.getNumberOfBands()

        for (equalizerBandIndex in 0 until numberFrequencyBands) {
            val prefKey = "$CONST_PREF_FREQ$equalizerBandIndex"
            val herz = preferences.getInt(prefKey, Int.MAX_VALUE)
            if (herz != Int.MAX_VALUE) {
                mEqualizer.setBandLevel(equalizerBandIndex.toShort(), herz.toShort())
            }
        }
    }
}
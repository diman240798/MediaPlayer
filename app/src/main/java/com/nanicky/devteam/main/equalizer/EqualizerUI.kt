package com.nanicky.devteam.main.equalizer

import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.Equalizer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.nanicky.devteam.R
import timber.log.Timber
import java.util.*

const val CONST_PREF_FREQ = "Frequency"
const val CONST_PREF_ENABLED = "EqualizerEnabled"
private const val TAG = "EqualizerUI"


class EqualizerUI {
    companion object {
        fun make(
            mLinearLayout: LinearLayout,
            mEqualizer: Equalizer,
            spinner: Spinner,
            preferences: SharedPreferences
        ) {

            val context = mLinearLayout.context
            //equalizer heading
            //equalizer heading
            val equalizerHeading = TextView(context)
            equalizerHeading.text = context.getString(R.string.equalizer)
            equalizerHeading.textSize = 20f
            equalizerHeading.gravity = Gravity.CENTER_HORIZONTAL
            mLinearLayout.addView(equalizerHeading)

            //get number frequency bands supported by the equalizer engine
            val numberFrequencyBands: Short = mEqualizer.getNumberOfBands()

            //get the level ranges to be used in setting the band level
            //get lower limit of the range in milliBels
            val lowerEqualizerBandLevel: Short = mEqualizer.getBandLevelRange().get(0)
            //get the upper limit of the range in millibels
            val upperEqualizerBandLevel: Short = mEqualizer.getBandLevelRange().get(1)

            //loop through all the equalizer bands to display the band headings, lower
            //& upper levels and the seek bars
            for (i in 0 until numberFrequencyBands) {
                buildBand(
                    i,
                    context,
                    mEqualizer,
                    mLinearLayout,
                    lowerEqualizerBandLevel,
                    upperEqualizerBandLevel,
                    spinner,
                    preferences
                )
            }
        }

        private fun buildBand(
            i: Int,
            context: Context?,
            mEqualizer: Equalizer,
            mLinearLayout: LinearLayout,
            lowerEqualizerBandLevel: Short,
            upperEqualizerBandLevel: Short,
            spinner: Spinner,
            preferences: SharedPreferences
        ) {
            val equalizerBandIndex = i.toShort()
            //frequency header for each seekBar
            val frequencyHeaderTextview = TextView(context)
            frequencyHeaderTextview.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            frequencyHeaderTextview.gravity = Gravity.CENTER_HORIZONTAL
            val prefKey = "$CONST_PREF_FREQ$equalizerBandIndex"
            val herz = preferences.getInt(prefKey, mEqualizer.getBandLevel(equalizerBandIndex).toInt())
            frequencyHeaderTextview.setText("$herz Hz")
            mLinearLayout.addView(frequencyHeaderTextview)
            //set up linear layout to contain each seekBar
            val seekBarRowLayout = LinearLayout(context)
            seekBarRowLayout.orientation = LinearLayout.HORIZONTAL
            //set up lower level textview for context seekBar
            val lowerEqualizerBandLevelTextview = TextView(context)
            lowerEqualizerBandLevelTextview.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lowerEqualizerBandLevelTextview.text = (lowerEqualizerBandLevel / 100).toString() + " dB"
            //set up upper level textview for context seekBar
            val upperEqualizerBandLevelTextview = TextView(context)
            upperEqualizerBandLevelTextview.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            upperEqualizerBandLevelTextview.text = (upperEqualizerBandLevel / 100).toString() + " dB"
            //            **********  the seekBar  **************
            //set the layout parameters for the seekbar
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            //create a new seekBar
            val seekBar = SeekBar(context)
            //give the seekBar an ID
            seekBar.id = i
            seekBar.layoutParams = layoutParams
            seekBar.max = upperEqualizerBandLevel - lowerEqualizerBandLevel
            //set the progress for this seekBar
            seekBar.progress = herz - lowerEqualizerBandLevel
            //change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) spinner.setSelection(0)
                    val newFreq = (progress + lowerEqualizerBandLevel).toShort()
                    mEqualizer.setBandLevel(equalizerBandIndex, newFreq)
                    frequencyHeaderTextview.setText("$newFreq Hz")
                    preferences.edit().putInt(prefKey, newFreq.toInt()).commit()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            //add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview)
            seekBarRowLayout.addView(seekBar)
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview)
            mLinearLayout.addView(seekBarRowLayout)
            //        show the spinner
            makePresets(spinner, mEqualizer, mLinearLayout)
        }

        private fun makePresets(equalizerPresetSpinner : Spinner, mEqualizer: Equalizer, mLinearLayout: LinearLayout) {
            //set up the spinner
            //set up the spinner
            val equalizerPresetNames = ArrayList<String>()

            val equalizerPresetSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                equalizerPresetSpinner.context,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames
            )
            equalizerPresetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            //get list of the device's equalizer presets
            //get list of the device's equalizer presets
            equalizerPresetNames.add(mLinearLayout.context.getString(R.string.custom))
            for (i in 0 until mEqualizer.getNumberOfPresets()) { // edit name "Cinema" or "Concert"
                equalizerPresetNames.add(mEqualizer.getPresetName(i.toShort()))
                //Log.d("SearchCountParams", String.valueOf( mEqualizer.getNumberOfPresets()));
            }

            equalizerPresetSpinner.adapter = equalizerPresetSpinnerAdapter

            //handle the spinner item selections
            equalizerPresetSpinner.setSelection(0)
            equalizerPresetSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    if (position == 0) return
                    //first list item selected by default and sets the preset accordingly
                    Timber.d("Using Preset $position")
                    mEqualizer.usePreset((position - 1).toShort())
                    //get the number of frequency bands for this equalizer engine
                    val numberFrequencyBands: Short = mEqualizer.getNumberOfBands()
                    //get the lower gain setting for this equalizer band
                    val lowerEqualizerBandLevel: Short =
                        mEqualizer.getBandLevelRange().get(0)
                    //set seekBar indicators according to selected preset
                    for (i in 0 until numberFrequencyBands) {
                        val equalizerBandIndex = i
                        val seekBar = mLinearLayout.findViewById(equalizerBandIndex) as SeekBar
                        //get current gain setting for this equalizer band
                        //set the progress indicator of this seekBar to indicate the current gain value
                        seekBar.progress = mEqualizer.getBandLevel(equalizerBandIndex.toShort()) - lowerEqualizerBandLevel
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { //not used
                }
            }
        }
    }
}

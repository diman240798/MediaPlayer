package com.nanicky.devteam.main.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.utils.ViewUtils
import org.koin.android.ext.android.inject
import kotlin.math.hypot


private val TAG = SettingsFragment::class.java.simpleName

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    private val colorChangeSharedObject: ColorChangeSharedObject by inject()

    private lateinit var container: ConstraintLayout

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById<ConstraintLayout>(R.id.container)

        val navigationIcon = view.findViewById<ImageButton>(R.id.navigationIcon)
        navigationIcon.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToNavigationDialogFragment()
            findNavController().navigate(action)
        }

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        if (key == "dark_theme") {
            container.postDelayed({
                setTheme(key)
            }, 300)

        }

    }

    private fun setTheme(key: String) {

        val w = container.measuredWidth
        val h = container.measuredHeight

        val darkThemeSwitch = findPreference(key) as SwitchPreference

        val finalRadius = hypot(w.toDouble(), h.toDouble()).toFloat()

        container.setBackgroundColor(
            if (darkThemeSwitch.isChecked) resources.getColor(R.color.colorBackgroundBlack) else resources.getColor(R.color.colorBackgroundWhite)
        )

        val anim = ViewAnimationUtils.createCircularReveal(container, w / 2, h / 2, 0f, finalRadius)
        anim.duration = 700L
        anim.start()

        container.postDelayed({
            colorChangeSharedObject.backgrColorBottomPlayBack.postValue(
                if (darkThemeSwitch.isChecked) R.color.fadedColorPrimaryBlack else R.color.fadedColorPrimaryWhite
            )
        }, 400)

        container.postDelayed({
            colorChangeSharedObject.backgrColorBottomNavView.postValue(
                if (darkThemeSwitch.isChecked) R.color.bottomNavViewBackgrColorBlack else R.color.bottomNavViewBackgrColorWhite
            )
        }, 500)

        container.postDelayed({
            ViewUtils.changeDarkTheme(darkThemeSwitch.isChecked)
        }, 600)

    }


    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
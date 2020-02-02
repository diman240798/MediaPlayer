package com.jadebyte.jadeplayer.main.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.utils.ViewUtils
import org.koin.android.ext.android.inject


private val TAG = SettingsFragment::class.java.simpleName

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = FrameLayout(context!!)

        val textView = TextView(ContextThemeWrapper(context, R.style.AppTheme_SectionTitle))
        textView.setText(R.string.settings)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
        textView.setTextColor(Color.BLACK)
        root.addView(
            textView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.START
            )
        )

        val navIcon = ImageView(ContextThemeWrapper(context, R.style.AppTheme_SectionIcon))
        navIcon.setImageResource(R.drawable.ic_nav)
        navIcon.setPadding(0, 40, 30, 0)

        root.addView(
            navIcon,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END
            )
        )

        val listView = ListView(context)
        listView.id = android.R.id.list
        root.addView(listView)

        return root
    }*/

    private val preferences: SharedPreferences by inject()

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
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
            val darkThemeSwitch = findPreference(key) as SwitchPreference
            ViewUtils.changeDarkTheme(darkThemeSwitch.isChecked)
        }

    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): RecyclerView? {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                if (view is ConstraintLayout) {
                    val views = getAllChildren(view)
                    val sectionTitle = views[1] as TextView
                    sectionTitle.setText(R.string.settings)

                    val navigationIcon = views[3] as ImageView
                    navigationIcon.setOnClickListener {
                        val action =
                            SettingsFragmentDirections.actionSettingsFragmentToNavigationDialogFragment()
                        findNavController().navigate(action)
                    }
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        })
        return recyclerView
    }

    private fun getAllChildren(v: View): ArrayList<View> {
        if (v !is ViewGroup) {
            val viewArrayList = ArrayList<View>()
            viewArrayList.add(v)
            return viewArrayList
        }
        val result = ArrayList<View>()
        val viewGroup = v
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            val viewArrayList = ArrayList<View>()
            viewArrayList.add(v)
            viewArrayList.addAll(getAllChildren(child))
            result.addAll(viewArrayList)
        }
        return result
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
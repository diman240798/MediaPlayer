// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference


/**
 * Created by Wilberforce on 2019-05-19 at 01:52.
 */
object ViewUtils {
    @ColorInt
    fun resolveAndroidColorAttr(c: Context, @AttrRes colorAttrRes: Int): Int {
        val context = WeakReference(c).get()
        val resolvedAttr = resolveThemeAttr(context, colorAttrRes)
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return if (context == null) Color.parseColor("#b0b1b2") else ContextCompat.getColor(context, colorRes)
    }


    @ColorInt
    fun resolveColorAttr(c: Context, @AttrRes colorAttrRes: Int): Int {
        val context = WeakReference(c).get()
        val resolvedAttr = resolveThemeAttr(context, colorAttrRes)
        return if (context == null) Color.parseColor("#b0b1b2") else ContextCompat.getColor(context, resolvedAttr.data)
    }



    private fun resolveThemeAttr(context: Context?, @AttrRes attrRes: Int): TypedValue {
        val value = TypedValue()
        context?.theme?.resolveAttribute(attrRes, value, true)
        return value
    }

    public fun postponeRecyclerViewEnterSharedElementTransitionForFragment(rv: RecyclerView, fragment: Fragment) {
        class MyPreDrawListener : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rv.viewTreeObserver.removeOnPreDrawListener(this)
                fragment.startPostponedEnterTransition()
                return true
            }
        }
        rv.apply {
            fragment.postponeEnterTransition()
            viewTreeObserver?.addOnPreDrawListener(MyPreDrawListener())
        }
    }

    fun changeDarkTheme(isDarkTheme: Boolean) {
        val theme = if (isDarkTheme) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}
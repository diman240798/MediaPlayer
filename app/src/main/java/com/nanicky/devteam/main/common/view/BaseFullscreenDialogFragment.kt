package com.nanicky.devteam.main.common.view

import android.os.Bundle
import com.nanicky.devteam.R

open class BaseFullscreenDialogFragment : BaseDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialogStyle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.AppTheme_DialogAnimation
    }

}
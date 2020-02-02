package com.nanicky.devteam.main.common.view

import androidx.fragment.app.DialogFragment
import com.nanicky.devteam.main.common.utils.Utils

open class BaseDialogFragment : DialogFragment() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, activity)
}
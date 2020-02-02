package com.nanicky.devteam.common

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.nanicky.devteam.main.common.utils.Utils

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    fun isPermissionGranted(permission: String): Boolean = Utils.isPermissionGranted(permission, this)
}
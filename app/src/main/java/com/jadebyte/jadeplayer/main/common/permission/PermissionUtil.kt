package com.jadebyte.jadeplayer.main.common.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.jadebyte.jadeplayer.R

class PermissionUtil {
    companion object {
        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            context?.let {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        fun requestRequiredPermissions(activity: Activity?, vararg permissions: String): AlertDialog? {
            activity?.let {
                val dialog = AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.permissions))
                    .setMessage(activity.getString(R.string.ask_permissions))
                    .setPositiveButton("Ок") { d, w ->
                        ActivityCompat.requestPermissions(activity, permissions, 654)
                    }
                    .setCancelable(true)
                    .create()
                dialog.window!!.attributes.windowAnimations = R.style.AppTheme_DialogAnimation
                dialog.show()
                return dialog
            }
            return null;
        }
    }
}
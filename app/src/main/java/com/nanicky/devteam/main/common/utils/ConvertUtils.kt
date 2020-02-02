package com.nanicky.devteam.main.common.utils

import android.util.Base64
import java.io.UnsupportedEncodingException


object ConvertUtils {
    /**
     * Convert string to base64-encoded string
     */
    fun stringToBase64(string: String): String {
        return try {
            Base64.encodeToString(string.toByteArray(), Base64.NO_WRAP)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }


    /**
     *  Converts long to int. Use in xml for data binding
     */
    @JvmStatic
    fun longToInt(long: Long): Int{
        return long.toInt()

    }
}
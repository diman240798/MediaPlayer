package com.jadebyte.jadeplayer.main.db.favourite

import androidx.room.TypeConverter
import java.util.*

class StringArrayConverter {
    @TypeConverter
    fun get_string(str: List<String?>?): String? {
        if (str == null) return null
        val pictures = StringBuilder()
        for (s in str) pictures.append(s).append(",")
        return pictures.toString()
    }

    @TypeConverter
    fun set_string(str: String?): List<String>? {
        return if (str == null) null else ArrayList(
            Arrays.asList(
                *str.split(
                    ","
                ).toTypedArray()
            )
        )
    }
}
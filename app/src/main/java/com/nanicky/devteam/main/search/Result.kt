package com.nanicky.devteam.main.search

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Result(@StringRes val title: Int, val type: Type, var hasResults: Boolean = false) : Parcelable

enum class Type {
    Songs, Albums, Artists, Genres, Playlists
}
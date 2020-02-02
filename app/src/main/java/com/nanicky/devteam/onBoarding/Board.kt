package com.nanicky.devteam.onBoarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Board(@DrawableRes val image: Int, @StringRes val title: Int, @StringRes val description: Int)
// Copyright (c) 2020 . Wilberforce Uwadiegwu. All Rights Reserved.
package com.nanicky.devteam.main.settings

import androidx.lifecycle.MutableLiveData
import com.nanicky.devteam.R

class ColorChangeSharedObject {
    var backgrColorBottomPlayBack: MutableLiveData<Int> = MutableLiveData(R.color.fadedColorPrimary)
    var backgrColorBottomNavView: MutableLiveData<Int> = MutableLiveData()
}
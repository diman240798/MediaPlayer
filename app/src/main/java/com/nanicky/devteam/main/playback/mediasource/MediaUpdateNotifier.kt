package com.nanicky.devteam.main.playback.mediasource

import com.nanicky.devteam.main.common.view.BaseMediaStoreViewModel

class MediaUpdateNotifier {
    var baseMediaStoreViewModel: BaseMediaStoreViewModel<*>? = null

    fun update() {
        if (baseMediaStoreViewModel != null) baseMediaStoreViewModel!!.update()
    }
}
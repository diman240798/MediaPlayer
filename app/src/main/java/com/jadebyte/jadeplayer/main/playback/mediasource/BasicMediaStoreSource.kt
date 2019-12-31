package com.jadebyte.jadeplayer.main.playback.mediasource

import android.content.Context
import android.provider.MediaStore
import com.jadebyte.jadeplayer.main.songs.basicSongsOrder
import com.jadebyte.jadeplayer.main.songs.basicSongsSelection
import com.jadebyte.jadeplayer.main.songs.basicSongsSelectionArg

class BasicMediaStoreSource(appCtx: Context) : MediaStoreSource(
    appCtx,
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    basicSongsSelection,
    arrayOf(basicSongsSelectionArg),
    basicSongsOrder
) {

}
// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// A little modification was made from the original file: https://raw.githubusercontent.com/googlesamples/android-UniversalMusicPlayer/master/common/src/main/java/com/example/android/uamp/media/library/MusicSource.kt

package com.jadebyte.jadeplayer.main.playback.mediasource

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntDef
import com.jadebyte.jadeplayer.main.playback.PlaybackService
import kotlinx.coroutines.flow.Flow


/**
 * Created by Wilberforce on 2019-08-19 at 22:09.
 *
 * Interface used by [PlaybackService] for looking up [MediaMetadataCompat] objects
 *
 *  Because Kotlin provides methods such [Iterable.find] and [Iterable.filter],
 *  this is a convenient interface to have on sources.
 */
interface MusicSource {
    /**
     *  Begins loading the data for this music source.
     */
    fun load(): Flow<MediaMetadataCompat>

}
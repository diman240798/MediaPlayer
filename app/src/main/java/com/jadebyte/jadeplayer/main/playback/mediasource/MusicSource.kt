// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// A little modification was made from the original file: https://raw.githubusercontent.com/googlesamples/android-UniversalMusicPlayer/master/common/src/main/java/com/example/android/uamp/media/library/MusicSource.kt

package com.jadebyte.jadeplayer.main.playback.mediasource

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.IntDef
import com.jadebyte.jadeplayer.main.playback.PlaybackService


/**
 * Created by Wilberforce on 2019-08-19 at 22:09.
 *
 * Interface used by [PlaybackService] for looking up [MediaMetadataCompat] objects
 *
 *  Because Kotlin provides methods such [Iterable.find] and [Iterable.filter],
 *  this is a convenient interface to have on sources.
 */
interface MusicSource : Iterable<MediaMetadataCompat> {

    /**
     *  Begins loading the data for this music source.
     */
    fun load()

    /**
     *  Method which will perform a given action after this [MusicSource] is ready to be used.
     *
     *  @param function A lambda expression to be called with a boolean parameter when the source is ready. `true`
     *  indicates the source was successfully prepared., `false` indicates an error occurred.
     */
    fun whenReady(function: (Boolean) -> Unit): Boolean

    /**
     * Handles searching a [MusicSource] from a focused voice search, often coming
     * from the Google Assistant.
     */
    fun search(query: String, bundle: Bundle): List<MediaMetadataCompat>
}

@IntDef(
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class State

/**
 *  State indicating the source was created, but no initialization has performed.
 */
const val STATE_CREATED = 1

/**
 * State indicating initialization of the source is in progress
 */
const val STATE_INITIALIZING = 2

/**
 * State indicating the source has been initialized and is ready to be used
 */
const val STATE_INITIALIZED = 3

/**
 * State indicating an error has occured
 */
const val STATE_ERROR = 4
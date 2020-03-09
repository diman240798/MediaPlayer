package com.nanicky.devteam.main.playback.mediasession

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator

// Helper class to retrieve the the Metadata necessary for the ExoPlayer MediaSession connection
// extension to call [MediaSessionCompat.setMetadata].
class QueueNavigator(mediaSession: MediaSessionCompat) :
    TimelineQueueNavigator(mediaSession) {

    private val window = Timeline.Window()

    override fun getMediaDescription(player: Player, windowIndex: Int) = player.currentTimeline.getWindow(windowIndex, window, true).tag as MediaDescriptionCompat

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean {
        return super.onCommand(player, controlDispatcher, command, extras, cb)
    }

    override fun onSkipToNext(player: Player?, controlDispatcher: ControlDispatcher?) {
        super.onSkipToNext(player, controlDispatcher)
    }

    override fun onSkipToPrevious(player: Player?, controlDispatcher: ControlDispatcher?) {
        super.onSkipToPrevious(player, controlDispatcher)
    }

}
package com.jadebyte.jadeplayer.main.playback.mediasession

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.common.utils.UriFileUtils
import com.jadebyte.jadeplayer.main.db.recently.RecentlyPlayed
import com.jadebyte.jadeplayer.main.playback.*
import com.jadebyte.jadeplayer.main.playback.mediasource.NOTIFICATION_LARGE_ICON_SIZE
import kotlinx.coroutines.launch

class MediaControllerCallback(private val playbackService: PlaybackService) : MediaControllerCompat.Callback() {
    private val notificationTarget = NotificationTarget()
    var largeBitmap: Bitmap? = null


    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        updateNotification(playbackService.mediaController.playbackState)
        addToRecentlyPlayed(metadata, playbackService.mediaController.playbackState)
        persistPosition()
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        updateNotification(state)
        addToRecentlyPlayed(playbackService.mediaController.metadata, state)
        persistPosition()
    }

    private fun updateNotification(state: PlaybackStateCompat?) {
        if (state == null) return

        when (val updatedState = state.state) {
            PlaybackStateCompat.STATE_PLAYING,
            PlaybackStateCompat.STATE_BUFFERING -> initiatePlayback(updatedState)
            else -> terminatePlayback(updatedState)
        }
    }

    // Removes the playback notification.
    // Since `stopForeground(false)` has already been called in [MediaControllerCallback.onPlaybackStateChanged],
    // it's possible to cancel the notification to cancel the notification with
    // `notificationManager.cancel(PLAYBACK_NOTIFICATION)` if minSdkVersion is >= [Build.VERSION_CODES.LOLLIPOP].
    //
    // Prior to [Build.VERSION_CODES.LOLLIPOP], notifications associated with a foreground service remained marked
    // "ongoing" even after calling [Service.stopForeground], and cannot be cancelled normally.
    //
    // Fortunately, it's possible to simple call [Service.stopForeground] a second time, this time with `true`.
    // his won't change anything about the service's state, but will simply remove the notification.
    internal fun removePlaybackNotification() = playbackService.stopForeground(true)

    private fun terminatePlayback(state: Int) {
        playbackService.becomingNoisyReceiver.unregister()
        if (playbackService.isForegroundService) {
            playbackService.stopForeground(false)
            playbackService.isForegroundService = false

            // If playback has ended, also stop the service
            if (state == PlaybackStateCompat.STATE_NONE) {
                playbackService.stopSelf()
            }
            val notification = buildNotification(state)
            if (notification != null) {
                playbackService.notificationManager.notify(Constants.PLAYBACK_NOTIFICATION, notification)
            } else {
                removePlaybackNotification()
            }
        }
    }

    private fun initiatePlayback(state: Int) {
        playbackService.becomingNoisyReceiver.register()

        // This may look strange, but the documentation for [Service.startForeground]
        // notes that "calling this method does *not* put the service in the started
        // state itself, even though the name sounds like it."
        buildNotification(state)?.let {
            playbackService.notificationManager.notify(Constants.PLAYBACK_NOTIFICATION, it)
            if (UriFileUtils.checkIconUri(playbackService.contentResolver, playbackService.mediaController.metadata.description.iconUri)) {
                loadLargeIcon(playbackService.mediaController.metadata.description.iconUri)
            } else {
                loadLargeIcon(res = R.drawable.ic_launcher)
            }

            if (!playbackService.isForegroundService) {
                ContextCompat.startForegroundService(
                    playbackService.applicationContext,
                    Intent(playbackService.applicationContext, PlaybackService::class.java)
                )
                playbackService.startForeground(Constants.PLAYBACK_NOTIFICATION, it)
                playbackService.isForegroundService = true
            }
        }
    }

    private fun loadLargeIcon(uri: Uri? = null, @DrawableRes res: Int = 0) {
        val drawable = uri ?: res;
        Glide.with(playbackService)
            .asBitmap()
            .skipMemoryCache(false)
            .load(drawable)
            .into(notificationTarget)
    }

    private fun buildNotification(state: Int): Notification? {

        // Skip building a notification when state is "none" and metadata is null
        val mediaController = playbackService.mediaController
        return if (mediaController.metadata != null && mediaController.metadata.description.title != null
            && state != PlaybackStateCompat.STATE_NONE
        ) {
            playbackService.notificationBuilder.buildNotification(playbackService.mediaSession.sessionToken, largeBitmap)
        } else {
            null
        }
    }

    private fun addToRecentlyPlayed(
        metadata: MediaMetadataCompat?,
        state: PlaybackStateCompat?
    ) {
        if (metadata?.id != null && state?.isPlaying == true) {
            playbackService.serviceScope.launch {
                val played =
                    RecentlyPlayed(
                        metadata
                    )
                val recentRepo = playbackService.recentRepo
                recentRepo.insert(played)
                recentRepo.trim()
                playbackService.preferences.edit().putString(Constants.LAST_ID, metadata.id).apply()
            }

        }
    }

    private fun persistPosition() {
        if (playbackService.mediaController.playbackState.started) {
            playbackService.preferences.edit().putLong(Constants.LAST_POSITION, playbackService.exoPlayer.contentPosition)
                .apply()
        }
    }


    private inner class NotificationTarget :
        CustomTarget<Bitmap>(
            NOTIFICATION_LARGE_ICON_SIZE,
            NOTIFICATION_LARGE_ICON_SIZE
        ) {

        override fun onStart() {
            largeBitmap = null
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            largeBitmap = null
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            largeBitmap = null
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val notification = playbackService.notificationBuilder.buildNotification(playbackService.mediaSession.sessionToken, resource)
            playbackService.notificationManager.notify(
                Constants.PLAYBACK_NOTIFICATION, notification
            )
            largeBitmap = resource
        }

    }


}
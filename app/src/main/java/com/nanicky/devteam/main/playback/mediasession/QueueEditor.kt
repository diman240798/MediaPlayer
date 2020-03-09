package com.nanicky.devteam.main.playback.mediasession

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.nanicky.devteam.main.playback.mediasource.BrowseTree


// Helper class to retrieve the the Metadata necessary for the ExoPlayer MediaSession connection
// extension to call [MediaSessionCompat.setMetadata].
class QueueEditor(
    val browseTree: BrowseTree,
    val dataSourceFactory: DataSource.Factory
) : MediaSessionConnector.QueueEditor {

    private val window = Timeline.Window()

    override fun onCommand(
        player: Player?,
        controlDispatcher: ControlDispatcher?,
        command: String?,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean {
        return false
    }

    override fun onRemoveQueueItem(
        player: Player?,
        description: MediaDescriptionCompat?
    ) { // TODO: Use Corutine
        val currentMediaSource = browseTree.currentMediaSource ?: return
        for (i in 0 until currentMediaSource.size) {
            val currentMediaUri =
                (currentMediaSource.getMediaSource(i).tag as MediaDescriptionCompat).mediaUri
            if (currentMediaUri == description?.mediaUri) {
                currentMediaSource.removeMediaSource(i)
                break
            }
        }
    }

    override fun onAddQueueItem(player: Player?, description: MediaDescriptionCompat?) {
        val currentMediaSource = browseTree.currentMediaSource ?: return
        val description = description ?: return
        currentMediaSource.addMediaSource(
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .setTag(description)
                .createMediaSource(description.mediaUri)
        )
    }

    override fun onAddQueueItem(player: Player?, description: MediaDescriptionCompat?, index: Int) {
        val currentMediaSource = browseTree.currentMediaSource ?: return
        val description = description ?: return
        currentMediaSource.addMediaSource(
            index,
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .setTag(description)
                .createMediaSource(description.mediaUri)
        )
    }

}
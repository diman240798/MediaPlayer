// Modifications was made from the original file: https://github.com/googlesamples/android-UniversalMusicPlayer/raw/master/common/src/main/java/com/example/android/uamp/media/MusicService.kt

package com.nanicky.devteam.main.playback

import android.app.PendingIntent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.nanicky.devteam.R
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.db.AppRoomDatabase
import com.nanicky.devteam.main.db.currentqueue.CurrentQueueSongsRepository
import com.nanicky.devteam.main.db.recently.RecentlyPlayedRepository
import com.nanicky.devteam.main.equalizer.EqualizerInitializer
import com.nanicky.devteam.main.playback.mediasession.MediaControllerCallback
import com.nanicky.devteam.main.playback.mediasession.QueueEditor
import com.nanicky.devteam.main.playback.mediasession.QueueNavigator
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.playback.mediasource.PlaybackPreparer
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

/**
 * This class is the entry point for browsing and playback commands from the APP's UI
 * and other apps that wish to play music via Player (for example, Android Auto or
 * the Google Assistant).
 *
 * Browsing begins with the method [PlaybackService.onGetRoot], and continues in
 * the callback [onLoadChildren].
 */
private const val TAG = "PlaybackService"

class PlaybackService : MediaBrowserServiceCompat() {
    internal lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var packageValidator: PackageValidator
    internal lateinit var mediaSession: MediaSessionCompat
    internal lateinit var mediaController: MediaControllerCompat
    internal lateinit var notificationManager: NotificationManagerCompat
    internal lateinit var notificationBuilder: NotificationBuilder
    private lateinit var mediaSessionConnector: MediaSessionConnector
    internal lateinit var recentRepo: RecentlyPlayedRepository

    private val currentSongRepository: CurrentQueueSongsRepository by inject()
    private val browseTree: BrowseTree by inject()

    private val serviceJob = SupervisorJob()
    internal val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    internal val preferences: SharedPreferences by inject()
    internal var isForegroundService = false


    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Creating service")

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingActivity =
            packageManager?.getLaunchIntentForPackage(packageName)?.let {
                PendingIntent.getActivity(this, 0, it, 0)
            }

        recentRepo =
            RecentlyPlayedRepository(
                AppRoomDatabase.getDatabase(application).recentDao()
            )

        // Create a MediaSession
        mediaSession = MediaSessionCompat(this, this.javaClass.name).apply {
            setSessionActivity(sessionActivityPendingActivity)
            isActive = true
        }

        // In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
        // a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
        // Note that this must be set by the time [onGetRoot] returns otherwise the connection will fail silently
        // and the system will not even call [MediaBrowserCompat.ConnectionCallback.onConnectionFailed]
        sessionToken = mediaSession.sessionToken

        // Because ExoPlayer will manage the MediaSession, add the service as a callback for
        // state changes.
        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback(this))
        }

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)
        becomingNoisyReceiver = BecomingNoisyReceiver(this, mediaSession.sessionToken)


        // The media library is built from the MediaStore. We'll create the source here, and then use
        // a suspend function to perform the query and initialization off the main thread

        serviceScope.launch {
            // ExoPlayer will manage the MediaSession for us.
            mediaSessionConnector = MediaSessionConnector(mediaSession).also {
                // Produces DataSource instances through which media data is loaded.
                val dataSourceFactory = FileDataSourceFactory()
                // Create the PlaybackPreparer of the media session connector.
                val playbackPreparer =
                    PlaybackPreparer(
                        browseTree,
                        exoPlayer,
                        dataSourceFactory,
                        currentSongRepository,
                        preferences
                    )
                it.setPlayer(exoPlayer)
                it.setPlaybackPreparer(playbackPreparer)
                it.setQueueNavigator(QueueNavigator(mediaSession))
                it.setQueueEditor(QueueEditor(browseTree, dataSourceFactory))
                it.mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                            or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                            or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )
            }
        }

        packageValidator = PackageValidator(this, R.xml.allowed_media_browser_callers)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.isActive = false
        mediaSession.release()

        // Cancel coroutines when the service is going away.
        serviceScope.cancel()
    }

    // Returns a list of [MediaItem]s that match the given search query
    override fun onSearch(query: String, extras: Bundle?, result: Result<List<MediaItem>>) {

        val resultList =
            browseTree.search(query, extras ?: Bundle.EMPTY)
                .map { MediaItem(it.description, it.flag) }
        if (resultList.isEmpty()) {
            result.detach()
        } else {
            result.sendResult(resultList)
        }
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>) {
        if (parentId == Constants.CURRENT_QUEUE_ROOT) {
            val currentQueueItems = currentSongRepository.get()?.map {
                MediaItem(it.description, it.flag)
            }
            if (currentQueueItems?.isEmpty() != false) {
                result.detach()
            } else {
                result.sendResult(currentQueueItems)
            }
            return
        }

        val children = browseTree[parentId]?.map {
            MediaItem(it.description, it.flag)
        }
        if (children?.isEmpty() ?: true) {
            result.detach()
        } else {
            result.sendResult(children)
        }

    }

    // Return  the "root" media ID that the client should request to get the list of [MediaItem]s to browse play
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // By default, all known clients are permitted to search, but only tell unknown callers about search if
        // permitted by the BrowserTree

        val isKnownCaller = packageValidator.isKnownCaller(clientPackageName, clientUid)
        val rootExtras = Bundle().apply {
            putBoolean(
                Constants.MEDIA_SEARCH_SUPPORTED,
                isKnownCaller || browseTree.searchableByUnknownCaller
            )
            putBoolean(Constants.CONTENT_STYLE_SUPPORTED, true)
            putInt(Constants.CONTENT_STYLE_BROWSABLE_HINT, Constants.CONTENT_STYLE_GRID)
            putInt(Constants.CONTENT_STYLE_PLAYABLE_HINT, Constants.CONTENT_STYLE_LIST)
        }

        return if (isKnownCaller) {
            // The caller is allowed to browse, so return the root
            BrowserRoot(Constants.BROWSABLE_ROOT, rootExtras)
        } else {
            // Unknown caller. Return a root without any content so the system doesn't disconnect the app
            BrowserRoot(Constants.EMPTY_ROOT, rootExtras)
        }
    }

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    internal val equalizerInitializer: EqualizerInitializer by inject()
    // Configure ExoPlayer to handle audio focus for us.
    // See https://link.medium.com/Zw5gorq9mZ
    internal val exoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this).apply {
            setAudioAttributes(this@PlaybackService.audioAttributes, true)
            addAnalyticsListener(object: AnalyticsListener {
                override fun onAudioSessionId(
                    eventTime: AnalyticsListener.EventTime?,
                    audioSessionId: Int
                ) {
                    super.onAudioSessionId(eventTime, audioSessionId)
                    equalizerInitializer.audioSessionId = audioSessionId
                    equalizerInitializer.initEqualizer()
                }
            })
        }
    }
}

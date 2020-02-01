// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.

package com.jadebyte.jadeplayer.main.common.data

/**
 * Created by Wilberforce on 2019-04-20 at 21:30.
 */
object Constants {
    // Keys for items in NavigationDialogFragment
    const val NAV_SONGS = "com.jadebyte.jadeplayer.nav.songs"
    const val NAV_IDENTIFY = "com.jadebyte.jadeplayer.nav.identify"
    const val NAV_ARTISTS = "com.jadebyte.jadeplayer.nav.artists"
    const val NAV_FAVOURITES = "com.jadebyte.jadeplayer.nav.favourites"
    const val NAV_GENRES = "com.jadebyte.jadeplayer.nav.genres"
    const val NAV_PLAYLIST = "com.jadebyte.jadeplayer.nav.playlist"
    const val NAV_RADIO = "com.jadebyte.jadeplayer.nav.radio"
    const val NAV_SETTINGS = "com.jadebyte.jadeplayer.nav.settings"
    const val NAV_VIDEOS = "com.jadebyte.jadeplayer.nav.videos"
    const val NAV_FOLDERS = "com.jadebyte.jadeplayer.nav.folders"
    const val NAV_WEB = "com.jadebyte.jadeplayer.nav.web"
    // Media playback
    const val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"


    const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
    const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
    const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
    const val CONTENT_STYLE_LIST = 1
    const val CONTENT_STYLE_GRID = 2
    const val BROWSABLE_ROOT = "/"
    const val EMPTY_ROOT = "@empty@"
    const val ALBUMS_ROOT = "__ALBUMS__"
    const val ARTISTS_ROOT = "_ARTISTS__"
    const val FOLDERS_ROOT = "_FOLDERS__"
    const val SONGS_ROOT = "_SONGS__"
    const val GENRES_ROOT = "_GENRES__"
    const val PLAYLISTS_ROOT = "__PLAYLISTS__"
    const val FAVOURITES_ROOT = "__FAVOUTITES__"
    const val NETWORK_FAILURE = "com.jadebyte.jadeplayer.playback.NETWORK_FAILURE"
    const val LAST_SHUFFLE_MODE = "com.jadebyte.jadeplayer.playback.LAST_SHUFFLE_MODE"
    const val LAST_REPEAT_MODE = "com.jadebyte.jadeplayer.playback.LAST_REPEAT_MODE"
    const val LAST_ID = "com.jadebyte.jadeplayer.playback.LAST_ID"
    const val LAST_POSITION = "com.jadebyte.jadeplayer.playback.LAST_POSITION"
    const val LAST_PARENT_ID = "com.jadebyte.jadeplayer.playback.LAST_PARENT_ID"
    const val PLAY_FIRST = "com.jadebyte.jadeplayer.playback.PLAY_FIRST"
    const val PLAY_RANDOM = "com.jadebyte.jadeplayer.playback.PLAY_RANDOM"
    const val PLAYBACK_NOTIFICATION: Int = 0xb2017


    // Other constants
    const val MAX_MODEL_IMAGE_THUMB_WIDTH = 100
    val WHITESPACE_REGEX = "\\s|\\n".toRegex()
    const val IMAGE_URI_ROOT = "android.resource://com.jadebyte.jadeplayer/drawable/"
    const val MAX_RECENTLY_PLAYED = 50
    const val SONG_DOWNLOAD_NOTIFICATION: Int = 0xb2019
    const val SONG_DOWNLOAD_NOTIFICATION_CHANNEL_ID: String = "Song Downloading Notification"

}

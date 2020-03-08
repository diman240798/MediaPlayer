package com.nanicky.devteam.main.search

import android.app.Application
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.WorkerThread
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.data.BaseMediaStoreRepository
import com.nanicky.devteam.main.common.data.Constants
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.playback.album
import com.nanicky.devteam.main.playback.albumArtist
import com.nanicky.devteam.main.playback.mediasource.BrowseTree
import com.nanicky.devteam.main.playback.title


class SearchRepository(
    application: Application,
    val browseTree: BrowseTree
) : BaseMediaStoreRepository(application) {

    @WorkerThread
    fun querySongs(query: String, ascend: Boolean): List<MediaMetadataCompat> =
        search(Constants.SONGS_ROOT, query, ascend)

    @WorkerThread
    fun queryAlbums(query: String, ascend: Boolean): List<Album> =
        search(Constants.ALBUMS_ROOT, query, ascend).map { Album(it) }

    @WorkerThread
    fun queryArtists(query: String, ascend: Boolean): List<Artist> =
        search(Constants.ARTISTS_ROOT, query, ascend).map { Artist(it) }

    @WorkerThread
    fun queryGenres(query: String, ascend: Boolean): List<Genre> =
        search(Constants.GENRES_ROOT, query, ascend).map { Genre(it) }


    @WorkerThread
    fun queryPlaylists(query: String, ascend: Boolean): List<Playlist> =
        search(Constants.PLAYLISTS_ROOT, query, ascend).map { Playlist(it) }

    private fun search(mediaId: String, query: String, ascend: Boolean): List<MediaMetadataCompat> {
        val songs = browseTree[mediaId]!!
        var filtered = songs.filter { contains(it, query) }
        if (!ascend) filtered = filtered.reversed()
        return filtered
    }

    private fun contains(it: MediaMetadataCompat?, query: String): Boolean {
        val titleMatch = it?.title?.toLowerCase()?.contains(query.toLowerCase()) ?: false
        val albumMatch = it?.album?.toLowerCase()?.contains(query.toLowerCase()) ?: false
        val artistMatch = it?.albumArtist?.toLowerCase()?.contains(query.toLowerCase()) ?: false
        return titleMatch || albumMatch || artistMatch
    }


}
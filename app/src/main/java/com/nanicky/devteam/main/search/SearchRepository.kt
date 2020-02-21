package com.nanicky.devteam.main.search

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.albums.baseAlbumProjection
import com.nanicky.devteam.main.albums.baseAlbumUri
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.data.BaseMediaStoreRepository
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.playback.mediasource.baseSongUri
import com.nanicky.devteam.main.playback.mediasource.basicSongsSelection
import com.nanicky.devteam.main.playback.mediasource.basicSongsSelectionArg
import com.nanicky.devteam.main.playlist.Playlist
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.baseSongsProjection


class SearchRepository(application: Application) : BaseMediaStoreRepository(application) {

    @WorkerThread
    fun querySongs(query: String, ascend: Boolean): List<Song> {
        val selection = "$basicSongsSelection AND ${MediaStore.Audio.Media.TITLE} LIKE ?"
        val selectionArgs = arrayOf(basicSongsSelectionArg, "%$query%")
        val order = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseSongUri, baseSongsProjection, selection, selectionArgs, order, ::Song)
    }

    @WorkerThread
    fun queryAlbums(query: String, ascend: Boolean): List<Album> {
        val selection = "${MediaStore.Audio.Media.ALBUM} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Media.ALBUM} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseAlbumUri, baseAlbumProjection, selection, selectionArgs, order, ::Album)
    }

    @WorkerThread
    fun queryArtists(query: String, ascend: Boolean): List<Artist> {
        val selection = "${MediaStore.Audio.Media.ARTIST} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Media.ARTIST} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseArtistUri, baseArtistProjection, selection, selectionArgs, order, ::Artist)
    }

    @WorkerThread
    fun queryGenres(query: String, ascend: Boolean): List<Genre> {
        val selection = "${MediaStore.Audio.Genres.NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Genres.NAME} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(baseGenreUri, baseGenreProjection, selection, selectionArgs, order, ::Genre)
    }


    @WorkerThread
    fun queryPlaylists(query: String, ascend: Boolean): List<Playlist> {
        val selection = "${MediaStore.Audio.Playlists.NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val order = "${MediaStore.Audio.Playlists.NAME} COLLATE NOCASE ${if (ascend) "ASC" else "DESC"}"
        return loadData(basePlaylistUri, basePlaylistProjection, selection, selectionArgs, order, ::Playlist)
    }

}


val baseGenreProjection = arrayOf(
    MediaStore.Audio.Genres._ID,
    MediaStore.Audio.Genres.NAME
)

val baseGenreUri: Uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI


val baseArtistProjection = arrayOf(
    MediaStore.Audio.Artists.ARTIST,
    MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
    MediaStore.Audio.Artists._ID
)

val baseArtistUri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI


val basePlaylistProjection = arrayOf(
    MediaStore.Audio.Playlists._ID,
    MediaStore.Audio.Playlists.NAME,
    MediaStore.Audio.Playlists.DATE_MODIFIED
)

val basePlaylistUri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
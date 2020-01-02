// Copyright (c) 2019 . Wilberforce Uwadiegwu. All Rights Reserved.
// // A little modification was made from the original file: https://raw.githubusercontent.com/googlesamples/android-UniversalMusicPlayer/master/common/src/main/java/com/example/android/uamp/media/library/BrowseTree.kt

package com.jadebyte.jadeplayer.main.playback.mediasource

import android.content.Context
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.common.urlEncoded
import com.jadebyte.jadeplayer.main.common.data.Constants
import com.jadebyte.jadeplayer.main.genres.Genre
import com.jadebyte.jadeplayer.main.playback.*
import com.jadebyte.jadeplayer.main.playlist.Playlist
import java.io.File


/**
 * Created by Wilberforce on 2019-08-19 at 22:08.
 *
 * Represents a tree of media that's used by [PlaybackService.onLoadChildren].
 *
 * [BrowseTree] maps a media id (see: [MediaMetadataCompat.METADATA_KEY_MEDIA_ID]) to one (or more)
 * [MediaMetadataCompat] objects, which are children of the media id.
 *
 * For example, given the following conceptual tree:
 * root
 *  +-- Albums
 *  |    +-- Album_A
 *  |    |    +-- Song_1
 *  |    |    +-- Song_2
 *  ...
 *  +-- Artists
 *  ...
 *
 * Requesting `browserTree["root"]` would return a list that included "Albums", "Artists", and any other direct
 * children. Taking the media ID of "Albums" ("Albums" in this example), `browseTree["Albums"]` would return a single
 * item list "Album_A", and, finally, `browseTree["Album_A"]` would return "Song_1" and "Song_2". Since those are leaf
 * nodes, requesting `browseTree["Song_1"]` would return null (there aren't any children of it).
 */
class BrowseTree(
    val musicSource: BasicMediaStoreSource,
    val playlistMediaSource: PlaylistMediaSource
) {
    var currentMediaSource: ConcatenatingMediaSource? = null

    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    operator fun get(parentId: String) = mediaIdToChildren[parentId]

    /**
     * Whether to allow clients which are unknown (non-whitelisted) to use search on this
     * [BrowseTree].
     */
    val searchableByUnknownCaller = true

    /**
     * In this example, there's a single root note (identified by the constant [Constants.BROWSABLE_ROOT].
     * The root's children are each album included in the [MusicSource],
     * and the childrenn of each album are songs on that album. See [buildAlbum] for details
     * TODO: Expand to allow more browsing types.
     */
    fun load(context: Context) {
        val rootList = mediaIdToChildren[Constants.BROWSABLE_ROOT] ?: mutableListOf()
        val songsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.SONGS_ROOT
            title = context.getString(R.string.songs)
            albumArtUri =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_song)
            flag = MediaItem.FLAG_PLAYABLE
        }.build()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.ALBUMS_ROOT
            title = context.getString(R.string.albums)
            albumArtUri =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_album)
        }.build()


        val artistsMetadata = MediaMetadataCompat.Builder().apply {
            id = Constants.ARTISTS_ROOT
            title = context.getString(R.string.artists)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_microphone)
        }.build()

        val foldersMetadata = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.FOLDERS_ROOT
            title = context.getString(R.string.folders)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_folder)
        }.build()

        val genresMetadata = MediaMetadataCompat.Builder().apply {
            // make able to play folder???
            id = Constants.GENRES_ROOT
            title = context.getString(R.string.genres)
            artist =
                Constants.IMAGE_URI_ROOT + context.resources.getResourceEntryName(R.drawable.ic_album)
        }.build()


        rootList += songsMetadata
        rootList += albumsMetadata
        rootList += artistsMetadata
        rootList += foldersMetadata
        rootList += genresMetadata

        val playlistsIds = mutableListOf<String>()
        playlistMediaSource.playlists.forEach {
            val playlistMetadata = MediaMetadataCompat.Builder().apply {
                id=it.id.toString()
                title=it.name
                flag = MediaItem.FLAG_BROWSABLE
            }.build()
            val playlistsList = mediaIdToChildren[Constants.PLAYLISTS_ROOT] ?: mutableListOf()
            playlistsList += playlistMetadata
            mediaIdToChildren[Constants.PLAYLISTS_ROOT] = playlistsList
            playlistsIds += it.id.toString()
        }

        mediaIdToChildren[Constants.BROWSABLE_ROOT] = rootList
        musicSource.forEach {
            val albumMediaId = it.albumId.urlEncoded
            val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(it)
            albumChildren += it


            val artistMediaId = it.artist.urlEncoded
            val artistChildren = mediaIdToChildren[artistMediaId] ?: buildArtistRoot(it)
            artistChildren += it

            val songsChildren = mediaIdToChildren[Constants.SONGS_ROOT] ?: mutableListOf()
            songsChildren += it
            mediaIdToChildren[Constants.SONGS_ROOT] = songsChildren

            val path = File(it.mediaUri.toString()).parent
            val foldersChildren = mediaIdToChildren[path] ?: buildFoldersRoot(it)
            foldersChildren += it


            val songId = it.id?.toInt()
            val genre = songId?.let { getGenreForSongBySongId(context, songId) }
            genre?.let { genre ->
                val genresChildren = mediaIdToChildren[genre.name] ?: buildGenresRoot(it, genre)
                genresChildren += it
            }

            it.id?.let {songId ->
                playlistMediaSource.playlists.forEach { playlist ->
                    if (playlist.songIds.contains(songId)) {
                        val url = playlist.id.urlEncoded
                        val playlistSongs = mediaIdToChildren[url] ?: mutableListOf()
                        playlistSongs += it
                        mediaIdToChildren[url] = playlistSongs
                    }
                }
            }
        }
    }

    private val genresProjection = arrayOf(
        MediaStore.Audio.Genres.NAME,
        MediaStore.Audio.Genres._ID
    )

    fun getGenreForSongBySongId(context: Context, songId: Int): Genre? {
        val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songId)
        var genresCursor = context.contentResolver.query(
            uri, genresProjection, null, null, null
        )
        genresCursor?.use {
            if (!genresCursor.moveToNext()) return null
            val id =
                genresCursor.getString(genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
            val name =
                genresCursor.getString(genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME))
            return Genre(id, name)
        }
        return null
    }

    /**
     * Builds a node, under the root, that represents an album, given
     * a [MediaMetadataCompat] object that's one of the songs on that album,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildAlbumRoot(metadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            albumId = metadata.albumId
            album = metadata.album
            artist = metadata.artist
            albumArt = metadata.albumArt
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this album to the 'Albums' category.
        val rootList = mediaIdToChildren[Constants.ALBUMS_ROOT] ?: mutableListOf()
        rootList += albumMetadata
        mediaIdToChildren[Constants.ALBUMS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[albumMetadata.albumId.urlEncoded] = it
        }
    }

    /**
     * Builds a node, under the root, that represents an artist, given
     * a [MediaMetadataCompat] object that's one of the songs on that artist,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildArtistRoot(metadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            id = metadata.artist.urlEncoded
            title = metadata.artist
            albumArt = metadata.albumArt
            //            albumArtUri = metadata.albumArtUri.toString()
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.ARTISTS_ROOT] ?: mutableListOf()
        rootList += artistMetadata
        mediaIdToChildren[Constants.ARTISTS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[artistMetadata.id!!] = it
        }
    }


    /** // TODO: FIX COMMENTS
     * Builds a node, under the root, that represents a folder, given
     * a [MediaMetadataCompat] object that's one of the songs on that folder,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildFoldersRoot(metadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val folderMetadata = MediaMetadataCompat.Builder().apply {
            id = metadata.artist.urlEncoded
            title = metadata.artist
            mediaUri = metadata.mediaUri.toString()
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.FOLDERS_ROOT] ?: mutableListOf()
        rootList += folderMetadata
        mediaIdToChildren[Constants.FOLDERS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        val path = File(metadata.mediaUri.toString()).parent
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[path] = it
        }
    }

    /** // TODO: FIX COMMENTS
     * Builds a node, under the root, that represents a folder, given
     * a [MediaMetadataCompat] object that's one of the songs on that folder,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildGenresRoot(
        metadata: MediaMetadataCompat,
        genre: Genre
    ): MutableList<MediaMetadataCompat> {
        val genreMetadata = MediaMetadataCompat.Builder().apply {
            id = genre.id
            title = genre.name
        }.build()

        // Adds this artist to the 'Artists' category.
        val rootList = mediaIdToChildren[Constants.GENRES_ROOT] ?: mutableListOf()
        rootList += genreMetadata
        mediaIdToChildren[Constants.GENRES_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[genre.name] = it
        }
    }
}
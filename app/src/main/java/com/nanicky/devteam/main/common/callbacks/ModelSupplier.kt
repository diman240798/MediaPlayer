package com.nanicky.devteam.main.common.callbacks

import android.support.v4.media.MediaMetadataCompat
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.data.Model
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.folders.Folder
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.songs.Song

interface ModelSupplier<T : Model> {
    fun get(data: MediaMetadataCompat) : T
}

class SongSuplier : ModelSupplier<Song> {
    override fun get(data: MediaMetadataCompat): Song = Song(data)
}

class AlbumSupplier : ModelSupplier<Album> {
    override fun get(data: MediaMetadataCompat): Album = Album(data)
}

class GenreSupplier : ModelSupplier<Genre> {
    override fun get(data: MediaMetadataCompat): Genre = Genre(data)
}

class ArtistSupplier : ModelSupplier<Artist> {
    override fun get(data: MediaMetadataCompat): Artist = Artist(data)
}

class PlaylistSupplier : ModelSupplier<Playlist> {
    override fun get(data: MediaMetadataCompat): Playlist = Playlist(data)
}

class FolderSupplier : ModelSupplier<Folder> {
    override fun get(data: MediaMetadataCompat): Folder = Folder(data)
}
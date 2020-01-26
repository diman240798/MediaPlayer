package com.jadebyte.jadeplayer.main.common.callbacks

import android.support.v4.media.MediaMetadataCompat
import com.jadebyte.jadeplayer.main.albums.Album
import com.jadebyte.jadeplayer.main.artists.Artist
import com.jadebyte.jadeplayer.main.common.data.Model
import com.jadebyte.jadeplayer.main.folders.Folder
import com.jadebyte.jadeplayer.main.genres.Genre
import com.jadebyte.jadeplayer.main.playlist.Playlist
import com.jadebyte.jadeplayer.main.songs.Song

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
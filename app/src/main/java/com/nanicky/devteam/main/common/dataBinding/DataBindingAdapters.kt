package com.nanicky.devteam.main.common.dataBinding

import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nanicky.devteam.R
import com.nanicky.devteam.main.albums.Album
import com.nanicky.devteam.main.artists.Artist
import com.nanicky.devteam.main.common.image.CircularTransparentCenter
import com.nanicky.devteam.main.common.utils.ImageUtils
import com.nanicky.devteam.main.db.playlist.Playlist
import com.nanicky.devteam.main.db.recently.RecentlyPlayed
import com.nanicky.devteam.main.genres.Genre
import com.nanicky.devteam.main.playback.MediaItemData
import com.nanicky.devteam.main.songs.Song
import java.io.File

object DataBindingAdapters {

    @JvmStatic val centerCrop = CenterCrop()
    @JvmStatic val circleCrop = CircleCrop()

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, song: Song?) {
        Glide.with(view)
            .load(R.drawable.thumb_circular_default)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .into(view)
        /*Glide.with(view)
            .load(song?.album)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)*/
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, mediaItem: MediaItemData?) {
        Glide.with(view)
            .load(R.drawable.thumb_circular_default)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .into(view)

        /*Glide.with(view)
            .load(mediaItem)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)*/
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, item: RecentlyPlayed?) {
        Glide.with(view)
            .load(R.drawable.thumb_circular_default)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .into(view)

        /*Glide.with(view)
            .load(item)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)*/
    }


    @BindingAdapter("mediaSrc")
    @JvmStatic
    fun setAlbumCoverCompat(view: ImageView, item: MediaItemData?) {
        Glide.with(view)
            .load(R.drawable.thumb_circular_default_hollow)
            .transform(
                MultiTransformation(centerCrop, circleCrop, CircularTransparentCenter(.3F))
            )
            .into(view)

        /*Glide.with(view)
            .load(item)
            .transform(
                MultiTransformation(centerCrop, circleCrop, CircularTransparentCenter(.3F))
            )
            .placeholder(R.drawable.thumb_circular_default_hollow)
            .into(view)*/
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setArtistAvatar(view: ImageView, artist: Artist) {
        Glide.with(view)
            .load(R.drawable.thumb_circular_default)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .into(view)
        /*Glide.with(view)
            .load(artist)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)*/
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setAlbumCover(view: ImageView, album: Album) {
        Glide.with(view)
            .load(R.drawable.thumb_default)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .into(view)

        /*Glide.with(view)
            .load(album)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default)
            .into(view)*/
    }

    @BindingAdapter("artistSrc")
    @JvmStatic
    fun setAlbumSrc(view: ImageView, album: Album) {
        Glide.with(view)
            .load(R.drawable.thumb_default_short)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .into(view)

        /*Glide.with(view)
            .load(album)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)*/
    }


    @BindingAdapter("android:src")
    @JvmStatic
    fun setPlaylistCover(view: ImageView, playlist: Playlist) {

        val imageFileForModel = ImageUtils.getImageFileForModelOrElse(
            playlist,
            R.drawable.thumb_circular_default
        )
        Glide.with(view)
            .load(imageFileForModel)
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(view)

        /*Glide.with(view)
            .load(playlist.modForViewWidth(view.measuredWidth))
            .transform(
                MultiTransformation(centerCrop, circleCrop)
            )
            .placeholder(R.drawable.thumb_circular_default)
            .into(view)*/
    }

    @BindingAdapter("playlistSrc")
    @JvmStatic
    fun setPlaylistSrc(view: ImageView, playlist: Playlist) {

        val imageFileForModel = ImageUtils.getImageFileForModelOrElse(
            playlist,
            R.drawable.thumb_default_short
        )
        Glide.with(view)
            .load(imageFileForModel)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(view)

        /*Glide.with(view)
            .load(playlist.modForViewWidth(view.measuredWidth))
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)*/
    }

    @BindingAdapter("genreSrc")
    @JvmStatic
    fun setGenreSrc(view: ImageView, genre: Genre) {
        Glide.with(view)
            .load(R.drawable.thumb_default_short)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .into(view)

        /*Glide.with(view)
            .load(genre)
            .transform(
                MultiTransformation(centerCrop, RoundedCorners(10))
            )
            .placeholder(R.drawable.thumb_default_short)
            .into(view)*/
    }

    @BindingAdapter("repeatSrc")
    @JvmStatic
    fun setRepeatModeSrc(view: ImageView, repeat: Int?) {
        val src = when (repeat) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
            PlaybackStateCompat.REPEAT_MODE_ONE -> R.drawable.ic_repeat_once
            else -> R.drawable.ic_repeat_none
        }
        view.setImageResource(src)
    }

    @BindingAdapter("shuffleSrc")
    @JvmStatic
    fun setShuffleModeSrc(view: ImageView, shuffle: Int?) {
        val src = if (shuffle == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
            R.drawable.ic_shuffle_off
        } else {
            R.drawable.ic_shuffle_on
        }
        view.setImageResource(src)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackgroundResource(view: View, @DrawableRes resource: Int) {
        view.setBackgroundResource(resource)
    }

    @BindingAdapter("enabled")
    @JvmStatic
    fun setEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
    }

}
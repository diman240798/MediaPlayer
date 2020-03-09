package com.nanicky.devteam.main.shazam.download

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import arrow.core.Try
import arrow.core.getOrDefault
import org.jsoup.Jsoup


class DownloadSongNotificationManager(
    var context: Context?,
    val author: String,
    val track: String
) {

    fun downloadSong() {
        context?.let { context ->
            val url = tryParseUrl(author, track) ?: return
            val dm: DownloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(
                Uri.parse(url)
            )
            request
                .setDescription(author)
                .setTitle(track)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "mediaViewerDownloads/shazam/$author - $track.mp3");

            dm.enqueue(request)
        }
    }

    private fun tryParseUrl(author: String, track: String): String? {
        val author = author.toLowerCase().replace(" ", "+")
        val track = track.toLowerCase().replace(" ", "+")

        return Try {
            getFromDriveMusic(author, track)
        }.getOrDefault {
            Try {
                getFromHitmo(author, track)
            }.getOrDefault {
                null
            }
        }
    }

    private fun getFromDriveMusic(author: String, track: String): String {
        val result = Jsoup
            .connect("https://ru-drivemusic.net/?do=search&subaction=search&story=$author+$track")
            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
            .get()
        return result.select("div").filter { it.className().equals("btn_player") }
            .get(0).select("a").attr("data-url").toString()
    }

    private fun getFromHitmo(author: String, track: String): String {
        val result = Jsoup
            .connect("https://ruru.hotmo.org//search?q=$author+$track")
            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
            .get()
        return result.select("a").filter { it.className().equals("track__download-btn") }
            .get(0).attr("href").toString()
    }
}
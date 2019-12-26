package com.jadebyte.jadeplayer.main.shazam.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import arrow.core.Try
import arrow.core.getOrDefault
import com.jadebyte.jadeplayer.main.common.data.Constants
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class DownloadSongNotificationManager(var context: Context?, val author: String, val track: String) {

    fun downloadSong() {
        val url = tryParseUrl() ?: return

        val conn = URL(url).openConnection()
        val inputStream = conn.getInputStream()

        val downloadDirPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath
        val filePath = "$downloadDirPath/$author - $track.mp3"

        val outouStream = FileOutputStream(File(filePath))

        val buffer = ByteArray(4096)
        val size = inputStream.available()
        var loaded = 0.0
        var len = 0

        while (true) {
            len = inputStream.read(buffer)
            if (len <= 0) break;
            outouStream.write(buffer, 0, len)
            loaded += len
            val percent: Double = loaded * 100 / size
            sendNotifocation(percent.toInt(), author, track)
        }
        outouStream.close()

        /*val result = Jsoup.connect("https://ru.hotmo.org/search?q=${resultTrack.artist}+${resultTrack.title}")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .get()
                val url = result.select("a").filter { it.className().equals("track__download-btn") }
                        .get(0).attr("href").toString()

                // get the file content
                val connection = Jsoup.connect(url).maxBodySize(0)
                val resultImageResponse = connection.ignoreContentType(true).execute()
                // save to file
                val contentType = resultImageResponse.contentType()
                val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/${resultTrack.artist} - ${resultTrack.title}.mp3"
                val out = FileOutputStream(filePath)
                out.write(resultImageResponse.bodyAsBytes())
                out.close()*/
    }

    private fun tryParseUrl(): String? {
        return Try {
            val result = Jsoup
                .connect("https://ru.hotmo.org/search?q=$author+$track")
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .get()
            result.select("a").filter { it.className().equals("track__download-btn") }
                .get(0).attr("href").toString()
        }.getOrDefault {
            null
        }
    }

    fun sendNotifocation(
        percent: Int = 0, author: String = "", track: String = ""
    ) {
        context = context ?: return
        val mBuilder =
            NotificationCompat.Builder(context!!, Constants.SONG_DOWNLOAD_NOTIFICATION_CHANNEL_ID)

        val smallIcon = when (percent < 100) {
            true -> android.R.drawable.stat_sys_download
            false -> android.R.drawable.stat_sys_download_done
        }
        mBuilder
            .setContentTitle(author)
            .setContentText(track)
            .setSmallIcon(smallIcon)
            .setProgress(100, percent, false)


        val mNotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Android Oreo Channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(mNotificationManager)
        }

        mNotificationManager.notify(Constants.SONG_DOWNLOAD_NOTIFICATION, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(mNotificationManager: NotificationManager) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val NOTIFICATION_CHANNEL_ID = Constants.SONG_DOWNLOAD_NOTIFICATION_CHANNEL_ID
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, importance
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        mNotificationManager.createNotificationChannel(notificationChannel)
    }
}

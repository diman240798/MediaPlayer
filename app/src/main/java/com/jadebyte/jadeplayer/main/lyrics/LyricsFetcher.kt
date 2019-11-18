package com.jadebyte.jadeplayer.main.lyrics

import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URLEncoder

class LyricsFetcher {

    companion object {

        data class FetchResult(val temp: String, val lyricsSource: String)
        class NoLyricsFoundException : Exception()

        fun fetchLyrics(id: String, artist: String, track: String): Lyrics {
            var fetchResult: FetchResult? = null

            val artistU = artist.replace(" ".toRegex(), "+")
            val trackU = track.replace(" ".toRegex(), "+")


            try {
                try {
                    fetchResult = tryAzlyrics(artistU, trackU)
                    if (fetchResult == null) throw NoLyricsFoundException()
                } catch (e: Exception) {
                    try {
                        fetchResult = tryGenius(artistU, trackU)
                        if (fetchResult == null) throw NoLyricsFoundException()
                    } catch (e: Exception) {
                        fetchResult = tryWikia(artistU, trackU)
                        if (fetchResult == null) throw NoLyricsFoundException()
                    }
                }

                var (temp, lyricsSource) = fetchResult!!

                temp = temp.replace("(?i)<br[^>]*>".toRegex(), "br2n")
                temp = temp.replace("]".toRegex(), "]shk")
                temp = temp.replace("\\[".toRegex(), "shk[")


                var lyrics = Jsoup.parse(temp).text()
                lyrics = lyrics.replace("br2n".toRegex(), "\n")
                lyrics = lyrics.replace("]shk".toRegex(), "]\n")
                lyrics = lyrics.replace("shk\\[".toRegex(), "\n [")
                if (lyricsSource.contains("Genius"))
                    lyrics = lyrics.substring(lyrics.indexOf("Lyrics") + 6)

                return Lyrics(
                    id,
                    artist,
                    track,
                    lyrics,
                    lyricsSource
                )


            } catch (e: Exception) {
                e.printStackTrace()
                return Lyrics(
                    "",
                    "",
                    "",
                    "Nothing Found",
                    "Sorry. We cdn't find lyrics for u :("
                )
            }
        }

        private fun tryAzlyrics(artistU: String, trackU: String): FetchResult? {

            var url = "https://www.google.com/search?q=" + URLEncoder.encode(
                "lyrics+azlyrics+$artistU+$trackU",
                "UTF-8"
            )
            var document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()
            var results = document.select("a").toList()
                .filter { it.attr("href").contains("https://www.azlyrics.com") }.first()

            var lyricURL: String =
                results.attr("href").substring(7, results.attr("href").indexOf("&"))

            if (lyricURL.contains("azlyrics.com/lyrics")) {
                document = Jsoup.connect(lyricURL)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .get()
                var page = document.toString()

                page = page.substring(page.indexOf("that. -->") + 9)
                page = page.substring(0, page.indexOf("</div>"))


                return FetchResult(page, "Azlyrics.com");
            }
            return null
        }

        private fun tryGenius(artistU: String, trackU: String): FetchResult? {

            val url = "https://www.google.com/search?q=" + URLEncoder.encode(
                "genius+" + artistU + "+" + trackU + "lyrics",
                "UTF-8"
            )
            var document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()

            val results = document.select("a").toList()
                .filter { it.attr("href").contains("https://genius") }.first()
            val lyricURL = results.attr("href").substring(7, results.attr("href").indexOf("&"))
            if (lyricURL.contains("genius")) {

                document = Jsoup.connect(lyricURL)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .get()

                val selector = document.select("div.h2")

                for (e in selector) {
                    e.remove()
                }

                val element = document.select("div[class=song_body-lyrics]").first()
                val temp =
                    element.toString()
                        .substring(0, element.toString().indexOf("<!--/sse-->"))

                return FetchResult(temp, "Genius.com")
            }
            return null
        }

        private fun tryWikia(artistU: String, trackU: String): FetchResult? {
            val url = "https://www.google.com/search?q=" + URLEncoder.encode(
                "lyrics.wikia+$trackU+$artistU",
                "UTF-8"
            )


            var document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()

            val results = document.select("h3.r > a").first() // TODO: FIX
            val lyricURL =
                results.attr("href").substring(7, results.attr("href").indexOf("&"))
            document = Jsoup.connect(lyricURL)
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .get()

            val element = document.select("div[class=lyricbox]").first()
            val temp = element.toString()
            return FetchResult(temp, "LyricsWikia")
        }
    }
}

fun main() {
    LyricsFetcher.fetchLyrics("10", "Nickelback", "How You Remind Me")
}

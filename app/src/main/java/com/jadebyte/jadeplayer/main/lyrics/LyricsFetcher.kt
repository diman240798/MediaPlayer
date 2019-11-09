package com.jadebyte.jadeplayer.main.lyrics

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Exception
import java.net.URLEncoder

class LyricsFetcher {

    companion object {

        fun fetchLyrics(id: String, artist: String, track: String): Lyrics {
            var lyricsSource: String? = null
            try {
                val artistU = artist.replace(" ".toRegex(), "+")
                val trackU = track.replace(" ".toRegex(), "+")
                var url = "https://www.google.com/search?q=" + URLEncoder.encode(
                    "lyrics+azlyrics+$artistU+$trackU",
                    "UTF-8"
                )
                var document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()
                var results = document.select("a").toList()
                    .filter { it.attr("href").contains("https://www.azlyrics.com") }.first()

                var lyricURL: String =
                    results.attr("href").substring(7, results.attr("href").indexOf("&"))
                val element: Element
                var temp: String

                if (lyricURL.contains("azlyrics.com/lyrics")) {
                    document = Jsoup.connect(lyricURL)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                        .get()
                    var page = document.toString()

                    page = page.substring(page.indexOf("that. -->") + 9)
                    page = page.substring(0, page.indexOf("</div>"))
                    temp = page

                    lyricsSource = "Azlyrics.com"

                } else {

                    url = "https://www.google.com/search?q=" + URLEncoder.encode(
                        "genius+" + artistU + "+" + trackU + "lyrics",
                        "UTF-8"
                    )
                    document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()

                    results = document.select("a").toList()
                        .filter { it.attr("href").contains("https://genius") }.first()
                    lyricURL = results.attr("href").substring(7, results.attr("href").indexOf("&"))
                    if (lyricURL.contains("genius")) {

                        document = Jsoup.connect(lyricURL)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .get()

                        val selector = document.select("div.h2")

                        for (e in selector) {
                            e.remove()
                        }

                        element = document.select("div[class=song_body-lyrics]").first()
                        temp =
                            element.toString()
                                .substring(0, element.toString().indexOf("<!--/sse-->"))

                        lyricsSource = "Genius.com"
                    } else {

                        url = "https://www.google.com/search?q=" + URLEncoder.encode(
                            "lyrics.wikia+$trackU+$artistU",
                            "UTF-8"
                        )


                        document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get()

                        results = document.select("h3.r > a").first() // TODO: FIX
                        lyricURL =
                            results.attr("href").substring(7, results.attr("href").indexOf("&"))
                        document = Jsoup.connect(lyricURL)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .get()

                        element = document.select("div[class=lyricbox]").first()
                        temp = element.toString()


                    }

                }

                temp = temp.replace("(?i)<br[^>]*>".toRegex(), "br2n")
                temp = temp.replace("]".toRegex(), "]shk")
                temp = temp.replace("\\[".toRegex(), "shk[")


                var lyrics = Jsoup.parse(temp).text()
                lyrics = lyrics.replace("br2n".toRegex(), "\n")
                lyrics = lyrics.replace("]shk".toRegex(), "]\n")
                lyrics = lyrics.replace("shk\\[".toRegex(), "\n [")
                if (lyricURL.contains("genius"))
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
    }
}

fun main() {
    LyricsFetcher.fetchLyrics("10", "Nickelback", "How You Remind Me")
}

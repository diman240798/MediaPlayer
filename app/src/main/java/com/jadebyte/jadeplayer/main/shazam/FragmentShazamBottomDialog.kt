package com.jadebyte.jadeplayer.main.shazam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jadebyte.jadeplayer.R
import com.jadebyte.jadeplayer.databinding.FragmentShazamResultDialogBinding
import com.jadebyte.jadeplayer.main.common.view.BaseMenuBottomSheet
import com.jadebyte.jadeplayer.main.shazam.model.ResultTrack
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Jsoup
import android.os.Environment
import java.io.FileOutputStream


private const val RESULT_TRACK_PARAM = "resultTrackJson"

class FragmentShazamBottomDialog : BaseMenuBottomSheet() {

    private lateinit var resultTrack: ResultTrack

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentShazamResultDialogBinding.inflate(inflater, container, false)
        binding.resutTrack = resultTrack
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val resultTrackJson = JSONObject(it.getString(RESULT_TRACK_PARAM))
            resultTrack = ResultTrack(resultTrackJson)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.share -> shareTrack()
            R.id.search -> searchTrack()
            R.id.searchAuthor -> searchAuthor()
            R.id.download -> download()
        }
    }

    private fun download() {
        val serviceJob = SupervisorJob()
        val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

        serviceScope.launch {
            val job = async {
                val result = Jsoup.connect("https://ru.hotmo.org/search?q=${resultTrack.artist}+${resultTrack.title}")
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
                out.close()
            }
        }
    }

    private fun searchAuthor() {

    }

    private fun searchTrack() {

    }

    private fun shareTrack() {

    }
}

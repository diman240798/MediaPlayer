package com.nanicky.devteam.main.shazam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.databinding.FragmentShazamResultDialogBinding
import com.nanicky.devteam.main.common.utils.Utils
import com.nanicky.devteam.main.common.view.BaseMenuBottomSheet
import com.nanicky.devteam.main.shazam.model.ResultTrack
import kotlinx.coroutines.*
import org.json.JSONObject
import com.nanicky.devteam.main.shazam.download.DownloadSongNotificationManager
import com.nanicky.devteam.main.songs.Song
import com.nanicky.devteam.main.songs.SongsMenuBottomSheetDialogFragmentDirections
import com.nanicky.devteam.main.web.WebFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val RESULT_TRACK_PARAM = "resultTrackJson"

class FragmentShazamBottomDialog : BaseMenuBottomSheet() {
    private lateinit var resultTrack: ResultTrack

    private val webVM: WebFragmentViewModel by sharedViewModel()

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
                val downloadNotificationManager = DownloadSongNotificationManager(context, resultTrack.artist, resultTrack.title)
                downloadNotificationManager.downloadSong()
            }
        }
    }

    private fun searchAuthor() {
// update vm
        webVM.setSearchString(resultTrack.artist)
        // change fragment
        val action =
            FragmentShazamBottomDialogDirections.actionFragmentShazamBottomDialogToWebFragment()
        findNavController().navigate(action)
    }

    private fun searchTrack() {
        webVM.setSearchString(resultTrack.title)
        // change fragment
        val action =
            FragmentShazamBottomDialogDirections.actionFragmentShazamBottomDialogToWebFragment()
        findNavController().navigate(action)
    }

    private fun shareTrack() {
        context?.also { context -> Utils.share(context, "${resultTrack.title} - ${resultTrack.artist}", resultTrack.artist, "Share Song") }
    }
}

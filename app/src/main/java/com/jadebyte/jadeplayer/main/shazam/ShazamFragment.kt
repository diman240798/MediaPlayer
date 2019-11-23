package com.jadebyte.jadeplayer.main.shazam


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jadebyte.jadeplayer.main.shazam.api.AuddAPI
import com.jadebyte.jadeplayer.main.shazam.api.AcrCloudApi
import com.jadebyte.jadeplayer.main.shazam.model.ResultTrack
import com.jadebyte.jadeplayer.main.shazam.ui.RecordView
import com.jadebyte.jadeplayer.main.shazam.ui.Screen
import com.jadebyte.jadeplayer.main.shazam.ui.SwitchView
import com.jadebyte.jadeplayer.main.shazam.ui.ToggleIcon
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class ShazamFragment : Fragment() {


    private var recordView: RecordView? = null
    private var isHumming = false
    private val songNetworkJob = SupervisorJob()
    private val songNetworkScope = CoroutineScope(Dispatchers.Main + songNetworkJob)


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = FrameLayout(context!!)


        recordView = RecordView(activity) { currentFile ->
            songNetworkScope.launch {
                var resultTrack = withContext(Dispatchers.IO) {
                    AcrCloudApi().recognizeVoice(currentFile, isHumming)
                }
                resultTrack = resultTrack ?: withContext(Dispatchers.IO) {
                    AuddAPI().recognizeVoice(currentFile, isHumming)
                }

                resultTrack = resultTrack ?: ResultTrack("We cdnt find that song(", "Sorry", "")
                val action = ShazamFragmentDirections.actionShazamFragmentToFragmentShazamBottomDialog(resultTrack.toString())
                findNavController().navigate(action)
                recordView?.setDefault()
            }
        }
        root.addView(
            recordView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )


        val singingSupplier = { isHumming }

        val toggleIcon = ToggleIcon(context, singingSupplier)
        val onSwitchListener = {
            isHumming = !isHumming
            toggleIcon.toggle()
        }

        val params = FrameLayout.LayoutParams(
            toggleIcon.size,
            toggleIcon.size,
            Gravity.CENTER or Gravity.BOTTOM
        )
        params.setMargins(0, 0, 0, Screen.dp(context!!, 24f))
        val switchView = SwitchView(singingSupplier, context)
        switchView.initActive(isHumming)
        switchView.setOnSwitchListener(onSwitchListener)
        root.addView(switchView, params)
        root.addView(toggleIcon, params)
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        songNetworkScope.cancel()
    }
}

package com.nanicky.devteam.main.shazam


import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nanicky.devteam.R
import com.nanicky.devteam.main.shazam.api.AcrCloudApi
import com.nanicky.devteam.main.shazam.api.AuddAPI
import com.nanicky.devteam.main.shazam.model.ResultTrack
import com.nanicky.devteam.main.shazam.ui.RecordView
import com.nanicky.devteam.main.shazam.ui.Screen
import com.nanicky.devteam.main.shazam.ui.SwitchView
import com.nanicky.devteam.main.shazam.ui.ToggleIcon
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class ShazamFragment : Fragment() {


    private var recordView: RecordView? = null
    private var isHumming = false
    private val songNetworkJob = SupervisorJob()
    private val songNetworkScope = CoroutineScope(Dispatchers.Main + songNetworkJob) // TODO MAIN???


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val root = FrameLayout(context!!)

        val textView = TextView(ContextThemeWrapper(context, R.style.AppTheme_SectionTitle))
        textView.setText(R.string.shazam)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
        textView.setTextColor(Color.BLACK)
        root.addView(
            textView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.START
            )
        )

        val navIcon = ImageView(ContextThemeWrapper(context, R.style.AppTheme_SectionIcon))
        navIcon.setImageResource(R.drawable.ic_nav)
        navIcon.setPadding(0, 40, 30, 0)
        navIcon.setOnClickListener {
            val action = ShazamFragmentDirections.actionShazamFragmentToNavigationDialogFragment()
            findNavController().navigate(action)
        }
        root.addView(
            navIcon,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.END
            )
        )


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

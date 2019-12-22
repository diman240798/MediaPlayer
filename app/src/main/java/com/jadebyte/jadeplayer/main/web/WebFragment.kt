package com.jadebyte.jadeplayer.main.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.jadebyte.jadeplayer.R
import kotlinx.android.synthetic.main.fragment_web.*

class WebFragment : Fragment() {

    private final val BASE_URL = "https://ru.hotmo.org/search?q="

    private lateinit var viewModel: WebFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOnBackPressed()
        setUpWebView()
        setUpVM()
    }

    private fun setUpWebView() {

    }

    private fun setUpVM() {
        viewModel = activity?.run { ViewModelProviders.of(this)[WebFragmentViewModel::class.java] }!!
        viewModel.searchString.observe(viewLifecycleOwner, Observer { updateViews(it) })
    }

    fun updateViews(searchStr: String) {
        webView.loadUrl("$BASE_URL$searchStr")
    }

    private fun setUpOnBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    this.isEnabled = false
                    activity?.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}

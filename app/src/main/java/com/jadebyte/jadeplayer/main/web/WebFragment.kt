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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WebFragment : Fragment() {


    private val viewModel: WebFragmentViewModel by sharedViewModel()

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
        viewModel.urls = resources.getStringArray(R.array.web_urls).toMutableList()
        viewModel.setUrl(viewModel.urls[0])
        viewModel.url.observe(viewLifecycleOwner, Observer { updateViews(it) })
    }

    fun updateViews(url: String) {
        webView.loadUrl("$url")
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

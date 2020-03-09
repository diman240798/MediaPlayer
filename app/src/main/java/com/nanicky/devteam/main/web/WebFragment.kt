package com.nanicky.devteam.main.web

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.nanicky.devteam.R
import com.nanicky.devteam.databinding.FragmentWebBinding
import com.nanicky.devteam.main.settings.ColorChangeSharedObject
import kotlinx.android.synthetic.main.fragment_web.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WebFragment : Fragment() {


    private val viewModel: WebFragmentViewModel by sharedViewModel()
    private val colorChangeSharedObject: ColorChangeSharedObject by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentWebBinding.inflate(inflater, container, false).let {
        it.viewModel = this@WebFragment.viewModel
        it.lifecycleOwner = viewLifecycleOwner
        return it.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOnBackPressed()
        setUpWebView()
        setUpVM()

        searchButton.setOnClickListener {
            viewModel.setSearchString(searchText.text.toString())
        }

        floatingActionButton.setOnLongClickListener {
            viewModel.setSearchString(searchText.text.toString())
            true
        }

        floatingActionButton.setOnClickListener {
            viewModel.isButtonsVisible.value = !viewModel.isButtonsVisible.value!!
        }

        drivemusicButton.setOnClickListener {
            viewModel.setUrl(viewModel.urls[0])
        }

        hotmoButton.setOnClickListener {
            viewModel.setUrl(viewModel.urls[1])
        }

        zaycevButton.setOnClickListener {
            viewModel.setUrl(viewModel.urls[2])
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.setOnScrollChangeListener(ScrollListener(floatingActionButton))
        }

        colorChangeSharedObject.isBottomNavVisible.value = false
    }

    private fun setUpWebView() {
        webView.setDownloadListener(object : DownloadListener {
            override fun onDownloadStart(
                url: String?,
                userAgent: String?,
                contentDisposition: String?,
                mimetype: String?,
                contentLength: Long
            ) {
                if (url?.endsWith(".mp3") == true) {
                    val request = DownloadManager.Request(
                        Uri.parse(url)
                    );
                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "mediaViewerDownloads/web/${url.substring(url.lastIndexOf("/"), url.length)}"
                    )
                    val dm = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)

                }
            }
        })
        webView.webViewClient = MyWebViewClient(context!!)
    }

    private fun setUpVM() {
        if (viewModel.searchString.value != null) {
            viewModel.urls = resources.getStringArray(R.array.web_urls_search).toMutableList()
        } else {
            viewModel.urls = resources.getStringArray(R.array.web_urls).toMutableList()
        }
        viewModel.url.observe(viewLifecycleOwner, Observer { updateViews(it) })
        viewModel.searchString.observe(viewLifecycleOwner, Observer { updateViews(it) })
        viewModel.setUrl(viewModel.urls[0])
    }

    fun updateViews(url: String?) {
        if (viewModel.searchString.value != null) {
            webView.loadUrl("${viewModel.url.value}${viewModel.searchString.value}")
        } else {
            webView.loadUrl(viewModel.url.value)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.searchString.value = null
        colorChangeSharedObject.isBottomNavVisible.value = true
    }
}

@RequiresApi(Build.VERSION_CODES.M)
class ScrollListener(val fab: FloatingActionButton) : View.OnScrollChangeListener {
    override fun onScrollChange(
        v: View?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY > oldScrollY && scrollY > 0) {
            fab.hide()
        }
        if (scrollY < oldScrollY) {
            fab.show()
        }
    }

}

class MyWebViewClient(val context: Context) : WebViewClient() {
    var pd = ProgressDialog(context)

    init {
        pd.setMessage("Please wait Loading...")
        pd.show()
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (!pd.isShowing) {
            pd.show()
        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (pd.isShowing) {
            pd.dismiss()
        }
        super.onPageFinished(view, url)
    }
}

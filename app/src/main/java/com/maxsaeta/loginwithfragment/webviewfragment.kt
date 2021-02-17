package com.maxsaeta.loginwithfragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.maxsaeta.loginwithfragment.databinding.FragmentWebviewloginBinding

import java.io.File


private const val LINK = "link"
private const val ADDR = "filesDir.absolutePath"

class webviewlogin : Fragment() {

    private lateinit var _binding: FragmentWebviewloginBinding
    private val binding get() = _binding

    private lateinit var pageUrl: String
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var pagetitle: String
    private lateinit var direccion: String
    private val urllogin = "https://user.manganelo.com/login"
    private var paginacargada: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) {
            paginacargada = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        paginacargada = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageUrl = it.getString(LINK).toString()
            direccion = it.getString(ADDR).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebviewloginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.isFocusableInTouchMode = true
        view.requestFocus()

        webView =  binding.fragwebView
        progressBar = binding.fragprogressBar
        swipeRefreshLayout = binding.fragswipe
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN)
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
            progressBar.visibility = ProgressBar.GONE
        }
        setWebClient()
        initWebView()
        loadUrl(pageUrl)

        val onBackPressed = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (webView.canGoBack())
                    webView.goBack()
                else if (activity != null)
                    requireActivity().onBackPressed()
            }

        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            onBackPressed,
            IntentFilter(this.hashCode().toString())
        )
    }

    private fun setWebClient() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress

                if (newProgress < 100 && progressBar.visibility == ProgressBar.GONE) {
                    paginacargada?.onUpdateProgress(newProgress)
                }

                if (newProgress == 100) {
                    progressBar.visibility = ProgressBar.GONE
                    swipeRefreshLayout.isRefreshing = false
                    pagetitle = webView.title.toString()
                    if (pagetitle == "Manganelo - Read Manga Online Free") {
                        loadUrl(urllogin)
                    } else if (pagetitle == "Bookmark - Manganelo") {
                        val archivo = "bookmark.txt"
                        webView.saveWebArchive(direccion + File.separator + archivo)
                        paginacargada?.onWebSaved()
                        webView.isVisible = false
                    } else if (pagetitle == "Webpage not available") {
                        paginacargada?.onWebNotAvailable()
                    }
                    if(pagetitle == "Login"){
                        webView.isVisible = true;
                    }
                    if(pagetitle.contains("=gohome")){
                        webView.isVisible = false;
                        loadUrl(pageUrl)
                    }
                    Log.e("Fragmente WebView", pagetitle)

                }
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.databaseEnabled = true
        webView.settings.setAppCachePath(requireContext().applicationContext.filesDir.absolutePath + "/cache")

        webView.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
        webView.run {
            webView.viewTreeObserver.addOnScrollChangedListener {
                swipeRefreshLayout.isEnabled = webView.scrollY == 0
            }
        }
    }

    private fun loadUrl(pageUrl: String) {
        webView.loadUrl(pageUrl)
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String, direccion: String) =
            webviewlogin().apply {
                arguments = Bundle().apply {
                    putString(LINK, url)
                    putString(ADDR, direccion)
                }
            }
    }
}
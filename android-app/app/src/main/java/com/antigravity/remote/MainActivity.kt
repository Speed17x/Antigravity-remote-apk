package com.antigravity.remote

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var fabSettings: FloatingActionButton
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "AntigravityRemotePrefs"
        private const val KEY_REMOTE_URL = "remote_url"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        webView = findViewById(R.id.webView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        fabSettings = findViewById(R.id.fabSettings)

        setupWebView()
        setupSwipeRefresh()
        setupBackNavigation()

        fabSettings.setOnClickListener {
            showUrlDialog()
        }

        val savedUrl = prefs.getString(KEY_REMOTE_URL, "")
        if (savedUrl.isNullOrBlank()) {
            showUrlDialog()
        } else {
            webView.loadUrl(savedUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                swipeRefresh.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                swipeRefresh.isRefreshing = false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.accent, R.color.primary)
        swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.surface_dark)
        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun showUrlDialog() {
        val currentUrl = prefs.getString(KEY_REMOTE_URL, "") ?: ""
        val input = EditText(this)
        input.hint = getString(R.string.dialog_url_hint)
        input.setText(currentUrl)
        input.setSelection(input.text.length)

        AlertDialog.Builder(this, com.google.android.material.R.style.Theme_MaterialComponents_DayNight_Dialog_Alert)
            .setTitle(R.string.dialog_url_title)
            .setMessage(R.string.dialog_url_message)
            .setView(input)
            .setPositiveButton(R.string.btn_save) { _, _ ->
                val newUrl = input.text.toString().trim()
                if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
                    prefs.edit().putString(KEY_REMOTE_URL, newUrl).apply()
                    webView.loadUrl(newUrl)
                    Toast.makeText(this, "Conectando...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "La URL debe comenzar con http:// o https://", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}

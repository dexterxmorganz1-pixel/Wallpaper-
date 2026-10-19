package com.aura.wallpapers

import android.app.Activity
import android.app.WallpaperManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun setWallpaper(url: String) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Wallpaper selection requested",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, "Android")

        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}


package com.aura.wallpapers

import android.app.Activity
import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(WallpaperBridge(), "AuraAndroid")
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class WallpaperBridge {
        @JavascriptInterface
        fun setWallpaper(imageUrl: String) {
            if (!imageUrl.startsWith("https://images.unsplash.com/")) {
                runOnUiThread { Toast.makeText(this@MainActivity, "Unsupported image source", Toast.LENGTH_SHORT).show() }
                return
            }
            executor.execute {
                try {
                    val connection = URL(imageUrl).openConnection() as HttpURLConnection
                    connection.connectTimeout = 15000
                    connection.readTimeout = 20000
                    connection.setRequestProperty("User-Agent", "AuraWallpapers/1.0")
                    connection.connect()
                    connection.inputStream.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                            ?: throw IllegalStateException("Could not decode image")
                        WallpaperManager.getInstance(this@MainActivity).setBitmap(bitmap)
                    }
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Wallpaper set successfully!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Could not set wallpaper. Check your internet connection.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        webView.destroy()
        super.onDestroy()
    }
}

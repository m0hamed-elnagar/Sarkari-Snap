package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppWebScreen(url: String, onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("In-app browser") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        BasicWebView(
            url = url,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            onOverrideUrlLoading = { newUrl ->
                // decide what to keep in this webview
                false   // keep everything inside
            }
        )
    }
}


/**
 * Minimal wrapper around Android WebView for Jetpack Compose.
 *  url: the page to load
 *  onOverrideUrlLoading: return `true` to keep the navigation inside the app
 */
@Composable
fun BasicWebView(
    url: String,
    modifier: Modifier = Modifier,
    onOverrideUrlLoading: (String) -> Boolean = { false }
) {
    val webView = remember {
        WebViewClientImpl(onOverrideUrlLoading)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = webView
                settings.javaScriptEnabled = false      // change to false if you don’t need JS
                loadUrl(url)
            }
        },
        modifier = modifier,
        update = { it.loadUrl(url) }
    )
}

private class WebViewClientImpl(
    private val onOverrideUrlLoading: (String) -> Boolean
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString() ?: return false
        return onOverrideUrlLoading(url)          // true = handled in-app
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        // you can expose loading state here if you want
    }
}
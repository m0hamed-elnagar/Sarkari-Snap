package com.example.taaza.today.bloger.ui.postDetails.componentes

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.izettle.html2bitmap.Html2Bitmap
import com.izettle.html2bitmap.content.WebViewContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch

@Composable
fun HtmlWebView(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cleaned = remember(html) {
        html.replace(Regex("background-color:[^;]+;?"), "")
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?is)<div[^>]*share|social|button|footer|ads|sponsor[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
    }

    val fullHtml = remember(cleaned) {
        """
        <html>
        <head>
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <style>
                body{font-family:sans-serif;font-size:17px;line-height:1.7;color:#333;
                     margin:0 10px;background:transparent;word-wrap:break-word}
                a{color:#1976d2;text-decoration:none}
                img{max-width:100%;height:auto}
                *{max-width:100%}
            </style>
        </head>
        <body>$cleaned</body>
        </html>
        """.trimIndent()
    }

    var heightPx by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(html) {
        heightPx = measureHtmlHeight(context, html, context.resources.displayMetrics.widthPixels)
    }


    // Fixed-height box that contains the *real* WebView (already detached)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightPx?.dp ?: 48.dp)

    ) {
        if (heightPx != null) {
            AndroidView(
                factory = {
                    WebView(context).apply {
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        isVerticalScrollBarEnabled   = false
                        isHorizontalScrollBarEnabled = false
                        overScrollMode               = WebView.OVER_SCROLL_NEVER
                        settings.javaScriptEnabled   = false
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                v: WebView?, req: WebResourceRequest?
                            ): Boolean {
                                req?.url?.let { onLinkClicked(it.toString()) }
                                return true
                            }
                        }
                        loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
}
suspend fun measureHtmlHeight(context: Context, html: String, screenWidth: Int): Int =
    withContext(Dispatchers.Main) {   // WebView must be on main thread
        val latch = CountDownLatch(1)
        var height = 0

        val webView = WebView(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
            isVerticalScrollBarEnabled   = false
            isHorizontalScrollBarEnabled = false
            overScrollMode               = WebView.OVER_SCROLL_NEVER
            with(settings) {
                javaScriptEnabled = false
                useWideViewPort   = true
                loadWithOverviewMode = true
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    view.post {
                        height = (view.contentHeight * view.resources.displayMetrics.density).toInt()
                        latch.countDown()
                    }
                }
            }
            layout(0, 0, screenWidth, 0) // off-screen
            loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        withContext(Dispatchers.IO) { latch.await() }   // wait for layout
        height
    }
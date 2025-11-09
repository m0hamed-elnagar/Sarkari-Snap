package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes


import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.WeakHashMap

object WebViewCache {
    private val cache = WeakHashMap<String, WebViewHolder0>()

    data class WebViewHolder0(
        val webView: WebView,
        var scrollX: Int = 0,
        var scrollY: Int = 0
    )

    fun getOrCreateWebView(
        context: Context,
        key: String,
        html: String,
        onLinkClicked: (String) -> Unit
    ): WebView {
        return cache.getOrPut(key) {
            val webView = WebView(context).apply {
                setBackgroundColor(TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = WebView.OVER_SCROLL_NEVER

                settings.apply {
                    javaScriptEnabled = false
                    loadsImagesAutomatically = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.url?.let { uri ->
                            onLinkClicked(uri.toString())
                        }
                        return true
                    }
                }

                loadHtmlContent(html, context)
            }
            WebViewHolder0(webView)
        }.also { holder ->
            // Restore scroll position when WebView is reused
            holder.webView.post {
                holder.webView.scrollTo(holder.scrollX, holder.scrollY)
            }
        }.webView
    }

    fun saveScrollPosition(key: String, webView: WebView) {
        cache[key]?.let { holder ->
            holder.scrollX = webView.scrollX
            holder.scrollY = webView.scrollY
        }
    }

    private fun WebView.loadHtmlContent(html: String, context: Context) {
        val cleanedHtml = html
            .replace(Regex("background-color:[^;]+;?"), "")
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )

            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
            .replace(
                Regex("(?s)\\A\\s*(<p[^>]*>(&nbsp;|\\s)*</p>|<div[^>]*>(&nbsp;|\\s)*</div>|<br\\s*/?>)+"),
                ""
            )
        val fullHtml = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: sans-serif;
                        font-size: 17px;
                        line-height: 1.7;
                        color: #333333;
                        margin: 0;
                        background-color: transparent;
                        word-wrap: break-word;
                        overflow-wrap: break-word;
                    }
                      p { margin-top: 0; } 
                    a {
                        color: #1976d2;
                        text-decoration: none;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                    }
                    * {
                        max-width: 100%;
                    }
                </style>
            </head>
            <body>$cleanedHtml</body>
            </html>
        """.trimIndent()

        this.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
    }
}

@Composable
fun PermanentHtmlContent2(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val compositionCount = remember { mutableStateOf(0) }


    val webViewKey = remember(html) { "webview_${html.hashCode()}" }
    LaunchedEffect(Unit) {
        compositionCount.value++
        println("PermanentHtmlContent recomposed: ${compositionCount.value} times")
    }

    val currentOnLinkClicked = remember(webViewKey) {
        object : (String) -> Unit {
            override fun invoke(url: String) {
                onLinkClicked(url)
            }
        }
    }

    // FIX: Use webViewKey instead of html in remember
    val webView = remember(webViewKey) {
        println("Creating new WebView for key: $webViewKey, html hash: ${html.hashCode()}")
        WebViewCache.getOrCreateWebView(context, webViewKey, html, currentOnLinkClicked)
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        factory = { webView },
        update = { view ->
            // Save scroll position when the view is about to be detached
            WebViewCache.saveScrollPosition(webViewKey, view)
        }
    )
}
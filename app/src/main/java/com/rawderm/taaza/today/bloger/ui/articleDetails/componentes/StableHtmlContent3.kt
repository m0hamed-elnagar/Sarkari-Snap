package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes

import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.WeakHashMap


@Composable
fun StableHtmlContent3(
    html: String,
    postId: String, // ← Add this
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webViewKey = remember(postId) { "webview_$postId" }
    val cachedHeight = remember { WebViewCache2.getCachedHeight(webViewKey) }

    // Estimate initial height based on content length to prevent jumps
    val estimatedHeight = remember(html) {
        val lines = (html.length / 100).coerceAtLeast(10)
        (lines * 24).dp
    }

    val actualHeight = remember { mutableStateOf(cachedHeight?.dp ?: estimatedHeight) }

    val currentOnLinkClicked = remember(webViewKey) { { url: String -> onLinkClicked(url) } }

    val webView = remember(webViewKey) {
        WebViewCache2.getOrCreateWebViewWithHeightCallback(
            context,
            webViewKey,
            html,
            currentOnLinkClicked,
            onHeightMeasured = { px ->
                val newHeightDp = (px / context.resources.displayMetrics.density).dp
                if (kotlin.math.abs(newHeightDp.value - actualHeight.value.value) > 50) {
                    actualHeight.value = newHeightDp
                }
            }
        )
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(actualHeight.value),
        factory = { webView },
        update = { WebViewCache2.saveScrollPosition(webViewKey, it) }
    )
}

// Enhanced WebViewCache with height measurement
object WebViewCache2 {
    private val cache = WeakHashMap<String, WebViewHolder>()
    private val heightCache = mutableMapOf<String, Int>()

    data class WebViewHolder(
        val webView: WebView,
        var scrollX: Int = 0,
        var scrollY: Int = 0,
        var measuredHeight: Int = 0
    )

    fun getOrCreateWebViewWithHeightCallback(
        context: Context,
        key: String,
        html: String,
        onLinkClicked: (String) -> Unit,
        onHeightMeasured: ((Int) -> Unit)? = null
    ): WebView {
        val holder = cache.getOrPut(key) {
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
                        request?.url?.let { uri -> onLinkClicked(uri.toString()) }
                        return true
                    }
                }

                /*  continuous height listener  */
                addContentHeightListener { px ->
                    heightCache[key] = px
                    cache[key]?.measuredHeight = px
                    onHeightMeasured?.invoke(px)
                }

                loadHtmlContent(html, context)
            }

            WebViewHolder(webView)
        }

        // Detach if already attached
        (holder.webView.parent as? android.view.ViewGroup)?.removeView(holder.webView)

        holder.webView.post {
            holder.webView.scrollTo(holder.scrollX, holder.scrollY)
        }
        heightCache[key]?.let { h ->
            onHeightMeasured?.invoke(h)
            holder.measuredHeight = h
        }

        return holder.webView
    }

    fun saveScrollPosition(key: String, webView: WebView) {
        cache[key]?.apply {
            scrollX = webView.scrollX
            scrollY = webView.scrollY
        }
    }

    fun getCachedHeight(key: String): Int? = heightCache[key]

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
                        margin: 0 10px;
                        background-color: transparent;
                        word-wrap: break-word;
                        overflow-wrap: break-word;
                        min-height: 100px;
                    }
                    a { color: #1976d2; text-decoration: none; }
                    img { max-width: 100%; height: auto; display: block; margin: 10px 0; }
                    * { max-width: 100%; }
                    p, div, h1, h2, h3, h4, h5, h6 { margin: 8px 0; }
                </style>
            </head>
            <body>$cleanedHtml</body>
            </html>
        """.trimIndent()

        this.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
    }
}

internal fun WebView.addContentHeightListener(onHeight: (Int) -> Unit) {
    // Always use the polling fallback – works on every API level
    val runnable = object : Runnable {
        override fun run() {
            if (isAttachedToWindow) {
                onHeight(contentHeight)
                postDelayed(this, 200L)
            }
        }
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            runnable.run()
        }

        override fun onViewDetachedFromWindow(v: View) {
            removeCallbacks(runnable)
        }
    })
    post { onHeight(contentHeight) }
}
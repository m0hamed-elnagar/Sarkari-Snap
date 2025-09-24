package com.example.taaza.today.bloger.ui.postDetails.componentes


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
fun StableHtmlContent4(
    html: String,
    postId: String, // ← Add this
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webViewKey = remember(postId) { "webview_$postId" } // ← Use postId
    val cachedHeight = remember { WebViewCache3.getCachedHeight(webViewKey) }

    // Estimate initial height based on content length to prevent jumps
    val estimatedHeight = remember(html) {
        val contentLength = html.length
        val estimatedLines = (contentLength / 100).coerceAtLeast(10) // Rough estimation
        (estimatedLines * 24).dp // 24dp per line estimate
    }

    val actualHeight = remember { mutableStateOf(cachedHeight?.dp ?: estimatedHeight) }

    val currentOnLinkClicked = remember(webViewKey) {
        object : (String) -> Unit {
            override fun invoke(url: String) = onLinkClicked(url)
        }
    }

    val webView = remember(webViewKey) {
        WebViewCache3.getOrCreateWebViewWithHeightCallback(
            context,
            webViewKey,
            html,
            currentOnLinkClicked,
            onHeightMeasured = { height ->
                // Only update if significantly different to prevent micro-adjustments
                val newHeightDp = (height / context.resources.displayMetrics.density).dp
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
        update = { view ->
            WebViewCache3.saveScrollPosition(webViewKey, view)
        }
    )
}

// Enhanced WebViewCache with height measurement
object WebViewCache3 {
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
                        request?.url?.let { uri ->
                            onLinkClicked(uri.toString())
                        }
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Measure height after page is loaded
                        view?.post {
                            view.evaluateJavascript("document.body.scrollHeight") { heightStr ->
                                try {
                                    val height = heightStr.replace("\"", "").toIntOrNull()
                                    if (height != null && height > 0) {
                                        // Convert to pixels
                                        val densityDpi = context.resources.displayMetrics.densityDpi
                                        val heightInPixels = (height * densityDpi / 160f).toInt()

                                        heightCache[key] = heightInPixels
                                        onHeightMeasured?.invoke(heightInPixels)

                                        // Store in holder as well
                                        cache[key]?.measuredHeight = heightInPixels
                                    }
                                } catch (e: Exception) {
                                    // Fallback to view's measured height
                                    val fallbackHeight = view.height
                                    if (fallbackHeight > 0) {
                                        heightCache[key] = fallbackHeight
                                        onHeightMeasured?.invoke(fallbackHeight)
                                        cache[key]?.measuredHeight = fallbackHeight
                                    }
                                }
                            }
                        }
                    }
                }

                loadHtmlContent(html, context)
            }

            WebViewHolder(webView)
        }

        // Detach if already attached
        (holder.webView.parent as? android.view.ViewGroup)?.removeView(holder.webView)

        // Restore scroll position and report cached height
        holder.webView.post {
            holder.webView.scrollTo(holder.scrollX, holder.scrollY)
        }
        heightCache[key]?.let { cachedHeight ->
            onHeightMeasured?.invoke(cachedHeight)
            holder.measuredHeight = cachedHeight
        }

        return holder.webView
    }
    fun saveScrollPosition(key: String, webView: WebView) {
        cache[key]?.let { holder ->
            holder.scrollX = webView.scrollX
            holder.scrollY = webView.scrollY
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
                        /* Prevent content jumping */
                        min-height: 100px;
                    }
                    a {
                        color: #1976d2;
                        text-decoration: none;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                        display: block;
                        margin: 10px 0;
                    }
                    * {
                        max-width: 100%;
                    }
                    /* Ensure consistent spacing */
                    p, div, h1, h2, h3, h4, h5, h6 {
                        margin: 8px 0;
                    }
                </style>
            </head>
            <body>$cleanedHtml</body>
            </html>
        """.trimIndent()

        this.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
    }
}
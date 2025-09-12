package com.example.taaza.today.bloger.ui.postDetails.componentes


import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PermanentHtmlContent(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    val contentKey = remember(html) { "html_${html.hashCode()}" }

    key(contentKey) {
        val webView = remember {
            WebView(context).apply {
                setBackgroundColor(TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER

                settings.apply {
                    javaScriptEnabled = false
                    loadsImagesAutomatically = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }

                // Use a stable reference for the callback
                val stableCallback = object : (String) -> Unit {
                    override fun invoke(url: String) {
                        onLinkClicked(url)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.url?.let { uri ->
                            stableCallback(uri.toString())
                        }
                        return true
                    }
                }

                loadHtmlContent(html, context, textColorArgb, linkColorArgb)
            }
        }


        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { webView }
            // No update block - it will never change
        )
    }
}

private fun WebView.loadHtmlContent(
    html: String,
    context: Context,
    textColorArgb: Int,
    linkColorArgb: Int
) {

    fun toCssColor(argb: Int): String {
        return String.format("#%06X", 0xFFFFFF and argb)
    }

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
                    color: ${toCssColor(textColorArgb)};
                    margin: 0 10px;
                    background-color: transparent;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                a {
                    color: ${toCssColor(linkColorArgb)};
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

package com.example.sarkarisnap.bloger.ui.postDetails.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView


@Composable
 fun HtmlContent(html: String,
                         onLinkClicked: (String) -> Unit,
                         modifier: Modifier = Modifier) {
    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    // Utility to convert ARGB → #RRGGBB
    fun toCssColor(argb: Int): String {
        return String.format("#%06X", 0xFFFFFF and argb)
    }

    // FIX 1: Create stable references to prevent unnecessary updates
    val cleanedHtml = remember(html) {
        html
            .replace(Regex("background-color:[^;]+;?"), "") // remove inline bg
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")


    }

    val fullHtml = remember(cleanedHtml, textColorArgb, linkColorArgb) {
        """
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
                    /* FIX 2: Prevent layout shifts */
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
                /* FIX 3: Prevent unwanted margins/padding */
                * {
                    max-width: 100%;
                }
            </style>
        </head>
        <body>$cleanedHtml</body>
        </html>
        """.trimIndent()
    }

    // FIX 4: Track if content has been loaded to prevent unnecessary reloads
    var hasLoaded by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            android.webkit.WebView(ctx).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                // FIX 5: Disable scrolling in WebView to prevent interference
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = android.view.View.OVER_SCROLL_NEVER

                // FIX 6: Configure WebView settings to prevent layout issues
                settings.apply {
                    javaScriptEnabled = false
                    loadsImagesAutomatically = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                }

                webViewClient = object : android.webkit.WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: android.webkit.WebView?,
                        request: android.webkit.WebResourceRequest?
                    ): Boolean {
                        request?.url?.let { uri ->
                            onLinkClicked(uri.toString())
                        }
                        return true
                    }

                    // FIX 7: Track when page finishes loading
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        hasLoaded = true
                    }
                }

                loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            // FIX 8: Only reload if HTML actually changed, not on every recomposition
            if (!hasLoaded || webView.url != "about:blank") {
                webView.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
            }
        }
    )
}

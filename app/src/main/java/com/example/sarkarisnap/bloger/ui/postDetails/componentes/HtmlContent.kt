package com.example.sarkarisnap.bloger.ui.postDetails.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        return String.format("#%06X", 0xFFFFFF and argb) }

    val cleanedHtml = remember(html) {
        html
            .replace(Regex("background-color:[^;]+;?"), "") // remove inline bg
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")


    }

    val fullHtml = remember(cleanedHtml) {
        """
        <html>
        <head>
            <style>
                body {
                    font-family: sans-serif;
                    font-size: 17px;
                    line-height: 1.7;
                    color: ${toCssColor(textColorArgb)};
                    margin: 0 10px;
                    background-color: transparent;
                }
                a {
                    color: ${toCssColor(linkColorArgb)};
text-decoration: none;
                }
            </style>
        </head>
        <body>$cleanedHtml</body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            android.webkit.WebView(ctx).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)

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
                }
                settings.javaScriptEnabled = false        // safe
                settings.loadsImagesAutomatically = true  // keep images
                loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
        }
    )
}

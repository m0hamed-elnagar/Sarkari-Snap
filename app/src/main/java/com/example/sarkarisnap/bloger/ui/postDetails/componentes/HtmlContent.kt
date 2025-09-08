package com.example.sarkarisnap.bloger.ui.postDetails.componentes

import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.viewinterop.AndroidView
@Composable
fun HtmlContent2(html: String,
                onLinkClicked: (String) -> Unit,
                modifier: Modifier = Modifier) {
    Text(
        AnnotatedString.fromHtml(
            html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontStyle = FontStyle.Italic,
                    color = Color.Blue
                )
            ),
            linkInteractionListener = {
                val url :String? = (it as LinkAnnotation.Url).url
               url?.let { uri ->
                    onLinkClicked(uri.toString())
                }
                // e.g., open in CustomTab
            }
        )
    )

}
@Composable
fun StableHtmlContent(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Use remember to store the WebView instance permanently
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

            // Load content immediately during creation
//            LoadHtmlContent(html, context)
        }
    }

    // This will never update after initial composition
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { webView }
        // No update block needed - it will never update
    )
}

@Composable
fun HtmlContent(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    // Utility to convert ARGB → #RRGGBB
    fun toCssColor(argb: Int): String {
        return String.format("#%06X", 0xFFFFFF and argb)
    }

    // Clean & simplify Blogger HTML
    val cleanedHtml = remember(html) {
        html
            .replace(Regex("background-color:[^;]+;?"), "") // remove inline bg
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
    }

    // Build final styled HTML
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
    }

    // Keep WebView alive across recompositions
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
        }
    }

    var hasLoaded by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { webView },
        update = {
            if (!hasLoaded) {
                it.loadDataWithBaseURL(null, fullHtml, "text/html", "UTF-8", null)
                hasLoaded = true
            }
        }
    )
}

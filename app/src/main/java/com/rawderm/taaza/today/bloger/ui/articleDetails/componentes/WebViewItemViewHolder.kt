package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView

class WebViewItemViewHolder(
    private val context: Context,
    private val onLinkClicked: (String) -> Unit
) : RecyclerView.ViewHolder(
    WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(Color.TRANSPARENT)
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        overScrollMode = WebView.OVER_SCROLL_NEVER
        with(settings) {
            javaScriptEnabled = false
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
        }
    }
) {
    private val webView get() = itemView as WebView

    fun bind(html: String) {
        val cleaned = html
            .replace(Regex("background-color:[^;]+;?"), "")
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?is)<div[^>]*share|social|button|footer|ads|sponsor[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")

        val full = """
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

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                v: WebView?,
                req: WebResourceRequest?
            ): Boolean {
                req?.url?.let { onLinkClicked(it.toString()) }
                return true
            }
        }
        webView.loadDataWithBaseURL(null, full, "text/html", "UTF-8", null)
    }
}
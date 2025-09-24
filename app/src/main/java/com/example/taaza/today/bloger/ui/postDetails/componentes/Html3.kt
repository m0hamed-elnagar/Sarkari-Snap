package com.example.taaza.today.bloger.ui.postDetails.componentes


import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.util.WeakHashMap
import android.content.Intent
import android.net.Uri
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat


@Composable
fun StableHtmlContent(
    html: String,
    postId: String,          // still here for future keys if you need them
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                setTextColor(0xFF333333.toInt())
                textSize = 17f
                setLineSpacing(0f, 1.7f)          // line-height: 1.7
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            textView.text = html.toClickableSpannable { url ->
                onLinkClicked(url)
                // optional: open browser yourself
                // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    )
}

/* ------------------------------------------------------------------ */
/* Helper – exactly the same visual cleaning we did for WebView before */
private fun String.toClickableSpannable(onUrlClick: (String) -> Unit): Spannable {
    // 1. visual cleaning -------------------------------------------------
    val cleaned = this
        .replace(Regex("</h[1-6]>"), "<br><br>")
        .replace(Regex("<h[1-6][^>]*>"), "")
        .replace("</p>", "<br><br>")
        .replace("<p>", "")
        .replace("<br>", "\n")
        .replace(Regex("<li[^>]*>"), "• ")
        .replace(Regex("</li>"), "\n")
        .replace(Regex("<ul[^>]*>|</ul>|</ol>|<ol[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace(Regex("<img[^>]*>"), "")
        .replace(Regex("<div[^>]*>|</div>"), "")
        .replace(Regex("<span[^>]*>|</span>"), "")

    // 2. parse remaining <b>, <i>, <a> … --------------------------------
    val spanned = HtmlCompat.fromHtml(cleaned, HtmlCompat.FROM_HTML_MODE_LEGACY)

    // 3. make links clickable -------------------------------------------
    val str = SpannableStringBuilder(spanned)
    val urls = str.getSpans(0, str.length, URLSpan::class.java)
    for (span in urls) {
        val start = str.getSpanStart(span)
        val end   = str.getSpanEnd(span)
        val flags = str.getSpanFlags(span)
        str.removeSpan(span)
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) = onUrlClick(span.url)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = 0xFF1976d2.toInt()
            }
        }, start, end, flags)
    }
    return str
}
@Composable
fun PermanentHtmlContent2(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
//    val context = LocalContext.current
//    val compositionCount = remember { mutableStateOf(0) }
//
//
//    val webViewKey = remember(html) { "webview_${html.hashCode()}" }
//    LaunchedEffect(Unit) {
//        compositionCount.value++
//        println("PermanentHtmlContent recomposed: ${compositionCount.value} times")
//    }
//
//    val currentOnLinkClicked = remember(webViewKey) {
//        object : (String) -> Unit {
//            override fun invoke(url: String) {
//                onLinkClicked(url)
//            }
//        }
//    }
//
//    // FIX: Use webViewKey instead of html in remember
//    val webView = remember(webViewKey) {
//        println("Creating new WebView for key: $webViewKey, html hash: ${html.hashCode()}")
//        WebViewCache9.getOrCreateWebView(context, webViewKey, html, currentOnLinkClicked)
//    }
//
//    AndroidView(
//        modifier = modifier.fillMaxWidth(),
//        factory = { webView },
//        update = { view ->
//            // Save scroll position when the view is about to be detached
//            WebViewCache2.saveScrollPosition(webViewKey, view)
//        }
//    )
}
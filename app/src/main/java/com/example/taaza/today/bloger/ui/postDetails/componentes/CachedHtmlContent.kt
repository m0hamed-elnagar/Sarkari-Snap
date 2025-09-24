package com.example.taaza.today.bloger.ui.postDetails.componentes



import android.content.Context
import android.graphics.Color.*
import android.graphics.Typeface
import android.os.Parcelable
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.CountDownLatch

@Composable
fun CachedHtmlContent(
    postId: String,
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 1.  cache holder (survives config-change)
    val cache = rememberSaveable { mutableMapOf<String, HtmlCache>() }

    // 2.  obtain or create
    val cached = remember(postId) {
        cache.getOrPut(postId) {
            // NEW post → measure off-screen
            val height = measureHtmlHeight(context, html)
            val webView = createWebView(context, html, onLinkClicked)
            HtmlCache(height, html)
        }
    }

    // 3.  trim to last 3
    LaunchedEffect(cache.size) {
        if (cache.size > 3) {
            val oldest = cache.keys.minus(cache.keys.sorted().takeLast(3))
            oldest.forEach { cache.remove(it) }
        }
    }

    // 4.  fixed-size box + cached WebView
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cached.height.dp)
    ) {
        AndroidView(factory = { createWebView(context, cached.webView, onLinkClicked) })
    }
}

/* ---------- data class + helpers ---------- */
@Parcelize
data class HtmlCache(val height: Int,  val webView: String) : Parcelable

fun measureHtmlHeight(context: Context, html: String): Int {
    val latch = CountDownLatch(1)
    var height = 0
    val webView = WebView(context).apply {
        setBackgroundColor(TRANSPARENT)
        isVerticalScrollBarEnabled   = false
        isHorizontalScrollBarEnabled = false
        overScrollMode               = View.OVER_SCROLL_NEVER
        settings.javaScriptEnabled   = false
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                view.post {
                    height = (view.contentHeight * resources.displayMetrics.density).toInt()
                    latch.countDown()
                }
            }
        }
        layout(0, 0, context.resources.displayMetrics.widthPixels, 0)
        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
    latch.await()
    return height
}

fun createWebView(context: Context, html: String, onLinkClicked: (String) -> Unit) =
    WebView(context).apply {
        setBackgroundColor(TRANSPARENT)
        isVerticalScrollBarEnabled   = false
        isHorizontalScrollBarEnabled = false
        overScrollMode               = View.OVER_SCROLL_NEVER
        settings.javaScriptEnabled   = false
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, req: WebResourceRequest?): Boolean {
                req?.url?.let { onLinkClicked(it.toString()) }
                return true
            }
        }
        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
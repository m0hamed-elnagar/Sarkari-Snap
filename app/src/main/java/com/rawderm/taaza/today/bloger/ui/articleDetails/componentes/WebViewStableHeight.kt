package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes

// Enhanced PermanentHtmlContent2 with stable height management

import android.text.Spanned
import android.text.style.URLSpan
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.compose.ui.res.colorResource

@Composable
fun StableHtmlContentStatic(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spanned = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val height = remember { mutableStateOf(200.dp) }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(height.value),
        factory = {
            android.widget.TextView(context).apply {
                setText(spanned)
                setTextColor(colorResource(android.R.color.black))
                textSize = 17f

                setPadding(0, 0, 0, 0)
                movementMethod = android.text.method.LinkMovementMethod.getInstance()

                // Handle clicks
                setOnClickListener {
                    val url = (text as? Spanned)
                        ?.getSpans(0, text.length, URLSpan::class.java)
                        ?.firstOrNull()?.url
                    url?.let(onLinkClicked)
                }

                // Measure height
                post {
                    val h = layout?.height ?: measuredHeight
                    height.value = (h / context.resources.displayMetrics.density).dp
                }
            }
        },
        update = { view ->
            view.setText(spanned, TextView.BufferType.SPANNABLE)
        }
    )
}
package com.rawderm.taaza.today.bloger.ui.postDetails.componentes


import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

@Composable
fun HtmlWebView11(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = LocalContentColor.current
    val stripped = html
        .replace(Regex("(?i)</?(p|div|h[1-6]|img|ul|ol|li|table|tr|td|th|tbody|thead|tfoot|br)[^>]*>"), " ")
        .replace(Regex("<a[^>]*>(\\s*)</a>"), " ")
        .replace(Regex("\\s{2,}"), " ")

    /* 2.  parse inline HTML */
    val spanned = HtmlCompat.fromHtml(stripped, HtmlCompat.FROM_HTML_MODE_COMPACT)

    /* 3.  build AnnotatedString with link areas ------------------------------- */
    val annotated = remember(spanned) {
        buildAnnotatedString {
            append(spanned.toString())

            spanned.getSpans(0, length, Any::class.java).forEach { span ->
                val start = spanned.getSpanStart(span)
                val end   = spanned.getSpanEnd(span)
                when (span) {
                    is StyleSpan -> when (span.style) {
                        Typeface.BOLD  -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        Typeface.ITALIC-> addStyle(SpanStyle(fontStyle  = FontStyle.Italic),start, end)
                    }
                    is UnderlineSpan ->
                        addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                    is URLSpan -> {
                        addStyle(
                            SpanStyle(
                                color = Color(0xFF1976D2),
                                textDecoration = TextDecoration.Underline
                            ),
                            start, end
                        )
                        addStringAnnotation(tag = "url", annotation = span.url, start = start, end = end)
                    }
                }
            }
        }
    }

    /* 4.  Text + manual tap handler (stable API) ------------------------------ */
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    androidx.compose.material3.Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.7),
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult.value?.let { res ->
                    val position = res.getOffsetForPosition(offset)
                    annotated.getStringAnnotations(tag = "url", start = position, end = position)
                        .firstOrNull()?.let { onLinkClicked(it.item) }
                }
            }
        },
        onTextLayout = { layoutResult.value = it }
    )
}
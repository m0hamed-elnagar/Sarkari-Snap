package com.rawderm.taaza.today.bloger.ui.articleDetails.componentes


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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat

@Composable
fun AnnotatedHtmlContent(
    html: String,
    onLinkClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    /* ---- 1.  same cleaner you already use ---- */
    val cleanedHtml = remember(html) {
        html
            .replace(Regex("background-color:[^;]+;?"), "")
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"), "")
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
    }

    /* 2.  convert block tags to plain text with markers -------------------------- */
    val prepared = remember(cleanedHtml) {
        cleanedHtml
            // headings → add newline + keep tag so we can style them
            .replace(Regex("(?i)<h1[^>]*>"), "\n\n<h1>")
            .replace(Regex("(?i)</h1>"), "</h1>\n\n")
            .replace(Regex("(?i)<h2[^>]*>"), "\n\n<h2>")
            .replace(Regex("(?i)</h2>"), "</h2>\n\n")
            .replace(Regex("(?i)<h3[^>]*>"), "\n\n<h3>")
            .replace(Regex("(?i)</h3>"), "</h3>\n\n")
            .replace(Regex("(?i)<h4[^>]*>"), "\n\n<h4>")
            .replace(Regex("(?i)</h4>"), "</h4>\n\n")
            .replace(Regex("(?i)<h5[^>]*>"), "\n\n<h5>")
            .replace(Regex("(?i)</h5>"), "</h5>\n\n")
            .replace(Regex("(?i)<h6[^>]*>"), "\n\n<h6>")
            .replace(Regex("(?i)</h6>"), "</h6>\n\n")
            // paragraphs → blank line
            .replace(Regex("(?i)</p>"), "</p>\n\n")
            // list items → bullet + newline
            .replace(Regex("(?i)<li[^>]*>"), "\n")
            .replace(Regex("(?i)</li>"), "")
            // ul/ol wrappers → just newlines
            .replace(Regex("(?i)</?(ul|ol)[^>]*>"), "\n")
            // <img> removed completely (no inline image support)
            .replace(Regex("(?i)<img[^>]*>"), "")
            // collapse multiple blanks
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    /* ---- 3.  parse inline HTML ---------------------------------------------------- */
    val spanned = remember(cleanedHtml) {
        HtmlCompat.fromHtml(cleanedHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    /* ---- 4.  build AnnotatedString (same colours as WebView) ---------------------- */
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = LocalContentColor.current

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
                    is URLSpan   -> {
                        // link colour = primary, NO underline (same as your CSS)
                        addStyle(SpanStyle(color = primary, textDecoration = TextDecoration.None), start, end)
                        addStringAnnotation(tag = "url", annotation = span.url, start = start, end = end)
                    }
                }
            }

            /* 5.  heading sizes (em units → identical to browser defaults) ------------- */
            val headingRegex = Regex("<h([1-6])>(.*?)</h[1-6]>")
            headingRegex.findAll(spanned.toString()).forEach { match ->
                val level = match.groupValues[1].toInt()
                val text  = match.groupValues[2]
                val start = match.range.first
                val end   = match.range.last + 1
                val sizeEm = when (level) {
                    1 -> 2.00f
                    2 -> 1.50f
                    3 -> 1.17f
                    4 -> 1.00f
                    5 -> 0.83f
                    6 -> 0.67f
                    else -> 1.00f
                }
                addStyle(
                    SpanStyle(fontSize = sizeEm.em, fontWeight = FontWeight.Bold),
                    start, end
                )
            }
        }
    }

    /* ---- 5.  Text with click handler (deterministic height) ----------------------- */
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 17.sp,              // CSS 17 px
        lineHeight = 17.sp * 1.7       // CSS line-height 1.7
    )

    androidx.compose.material3.Text(
        text = annotated,
        style = textStyle,
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
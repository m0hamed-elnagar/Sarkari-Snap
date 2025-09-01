package com.example.sarkarisnap.bloger.ui.postDetails

import android.content.Intent
import android.net.Uri
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.text.parseAsHtml
import coil3.compose.rememberAsyncImagePainter
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.ui.components.postImagePainter
import com.ireward.htmlcompose.HtmlText
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText


@Composable
fun PostDetailsScreenRoot(
    viewModel: PostDetailsViewModel = koinViewModel(), onBackClicked: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailsScreen(
        state = state, onAction = { action ->
            when (action) {
                is PostDetailsActions.OnBackClick -> onBackClicked()
                else -> Unit
            }
            viewModel.onAction(action)
        })
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailsScreen(
    state: PostDetailsState,
    onAction: (PostDetailsActions) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Article") }, navigationIcon = {
                IconButton(onClick = { onAction(PostDetailsActions.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            val post = state.post
            if (post == null) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {/* Title */
                post.imageUrls?.forEach { coverImage ->
                    val painter = postImagePainter(coverImage)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp, top = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }


                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                /* Meta line */
                Text(
                    text = "Updated: ${post.date}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                /* Body */
                HtmlContent5(
                    html = post.content, modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HtmlContent4(html: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    val styledHtml = remember(html) {
        html
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
            // spacing
            .replace("<p", "<br><br><p")
            .replace("</p>", "</p><br><br>")
            // link colour
            .replace(
                "<a",
                "<a style=\"color:${
                    Integer.toHexString(linkColorArgb).takeLast(6)
                };text-decoration:underline;\""
            )
    }

    val annotated = remember(styledHtml) {
        HtmlCompat.fromHtml(styledHtml, HtmlCompat.FROM_HTML_MODE_COMPACT)
            .toAnnotatedString()
    }

    ClickableText(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        ),
        modifier = modifier,
        onClick = { offset ->
            annotated
                .getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { item ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.item)))
                }
        }
    )
}

/* ---------- helpers ---------- */

private fun Spanned.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString)
        val spans = this@toAnnotatedString.getSpans(0, length, URLSpan::class.java)
        for (span in spans) {
            val start = getSpanStart(span)
            val end = getSpanEnd(span)
            addStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ),
                start = start,
                end = end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = span.url,
                start = start,
                end = end
            )
        }
    }
}

@Composable
private fun HtmlContent3(html: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.primary.toArgb()
    // 1. 100 % identical cleaning you already wrote
    val cleanedHtml = remember(html) {
        html
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
            // spacing
            .replace("<p", "<br><br><p")
            .replace("</p>", "</p><br><br>")

    }
    val finalHtml = remember(html) {
        val cleaned = html
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
            .replace("<p", "<br><br><p")
            .replace("</p>", "</p><br><br>")

        // Inject CSS for links
        """
    <style>
        a {
            color: ${Integer.toHexString(textColor).takeLast(6)};
            text-decoration: underline;
        }
    </style>
    """.trimIndent() + cleaned
    }
    // 2. Compose-HTML renderer
    HtmlText(
        text = finalHtml,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp,

            ),

        linkClicked = { url ->
            // open in the default browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    )
}

@Composable
private fun HtmlContent5(html: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    // Utility to convert ARGB → #RRGGBB
    fun toCssColor(argb: Int): String {
        return String.format("#%06X", 0xFFFFFF and argb)
    }

    val cleanedHtml = remember(html) {
        html
            .replace(Regex("background-color:[^;]+;?"), "") // remove inline bg

            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")

//            .replace("<p", "<br><br><p")
//            .replace("</p>", "</p><br><br>")
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
                        request?.url?.let {
                            context.startActivity(Intent(Intent.ACTION_VIEW, it))
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

@Composable
private fun HtmlContent2(
    html: String, modifier: Modifier = Modifier
) {
    val richState = rememberRichTextState()

    // Convert HTML → RichTextState once
    LaunchedEffect(html) {
//    val styledHtml = html
//        .replace("</a><a", "</a> <a")
//        .replace("<br>",  "<br/>")
//        .replace("<br/>", "\n")
//        .replace(Regex("(?s)<div class=\"addtoany_share_save_container.*?</div>"), "")
//        .replace(
//            Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor)[^>]*>.*?</div>"),
//            ""
//        )


        val cleanedHtml4 = html
            // Remove empty paragraphs
            .replace(Regex("<p>(&nbsp;|\\s)*</p>"), "")
            // Remove share/toc/ads containers
            .replace(
                Regex("(?s)<div[^>]*(share|social|button|footer|ads|sponsor|toc)[^>]*>.*?</div>"),
                ""
            )
            // Remove TOC spans
            .replace(Regex("<span[^>]*(ez-toc-section|ez-toc-section-end)[^>]*></span>"), "")
            .replace("</a><a", "</a> <a")
            // 3. inject <br><br> before every paragraph
            .replace("<p", "<br><br><p")
            //for after every block element
            .replace("</p>", "</p><br><br>")
        // 4. make sure <br> is self-closed
//            .replace("<br>", "<br/>")


        richState.setHtml(cleanedHtml4)
        richState.addParagraphStyle(

            ParagraphStyle(
                lineBreak = LineBreak.Paragraph,
//                lineHeightStyle = LineHeightStyle. ),
            )
        )

    }


    // Read-only rich text (no cursor)
    RichText(
        state = richState,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = colorScheme.onSurface,
            lineHeight = 16.sp // this increases spacing between lines & paragraphs

        ),

        )
}

@Composable
fun postImagePainter(imageUrl: String) = rememberAsyncImagePainter(
    model = imageUrl,
    placeholder = painterResource(com.example.sarkarisnap.R.drawable.news_placeholder),
    error = painterResource(R.drawable.news_placeholder),
    contentScale = ContentScale.Crop
)

@Composable
private fun HtmlContent(
    html: String, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fixedHtml = remember(html) {
        html.replace(Regex("</a><a"), "</a> <a")
    }
    val spanned by produceState<CharSequence?>(initialValue = null, fixedHtml) {
        value = withContext(Dispatchers.Default) {
            fixedHtml.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    val textColorArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    AndroidView(modifier = modifier, factory = {
        TextView(it).apply {
            movementMethod = LinkMovementMethod.getInstance()
            setTextAppearance(android.R.style.TextAppearance_Material_Body1)
            setTextColor(textColorArgb)
            setLinkTextColor(linkColorArgb)
            setLineSpacing(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
                ), 1f
            )
        }
    }, update = { textView ->
        spanned?.let { textView.text = it }
    })
}
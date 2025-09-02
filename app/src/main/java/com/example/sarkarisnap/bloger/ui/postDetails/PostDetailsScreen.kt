package com.example.sarkarisnap.bloger.ui.postDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sarkarisnap.bloger.ui.components.postImagePainter
import com.example.sarkarisnap.core.utils.openUrlInCustomTab
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PostDetailsScreenRoot(
    viewModel: PostDetailsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},

) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailsScreen(
        state = state, onAction = { action ->
            when (action) {
                is PostDetailsActions.OnBackClick -> onBackClicked()
                is PostDetailsActions.OnLinkClicked -> {
                    openUrlInCustomTab(context, action.url)

                }
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
                    //todo add to favorite

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
                    html = post.content,
                    onLinkClicked = { url ->
                        onAction(PostDetailsActions.OnLinkClicked(url))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                //todo things u might like
            }
        }
    }
}

@Composable
private fun HtmlContent5(html: String,
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
                                view?.loadUrl(request?.url.toString())
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

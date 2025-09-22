package com.example.taaza.today.bloger.ui.pageDetails

import android.R.attr.maxHeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.size.Size
import com.example.taaza.today.R
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.ui.postDetails.componentes.PermanentHtmlContent2
import com.example.taaza.today.core.ui.theme.SandYellow
import com.example.taaza.today.core.utils.openUrlInCustomTab
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PageDetailsScreenRoot(
    viewModel: PageDetailsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},

    ) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()


    PageDetailsScreen(
        state = state,

        onAction = { action ->
            when (action) {
                is PageDetailsActions.OnBackClick -> onBackClicked()
                is PageDetailsActions.OnLinkClicked -> openUrlInCustomTab(context, action.url)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageDetailsScreen(
    state: PageDetailsState,
    onAction: (PageDetailsActions) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val stableOnLinkClicked = rememberUpdatedState { url: String ->
        onAction(PageDetailsActions.OnLinkClicked(url))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { state.page?.title?.let { Text(it) } },
                navigationIcon = {
                    IconButton(onClick = { onAction(PageDetailsActions.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SandYellow,
                )
            )
        },

        ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            val page = state.page
            if (page == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
                return@Scaffold
            }
            // Use a single LazyColumn for the whole screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = scrollState
            ) {
                // --- Hero image(s) ---
                items(page.imageUrls.size) { idx ->
                    val url = page.imageUrls[idx]
                    val painter = postImagePainter(url)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }


                // ----- date -----
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Updated: ${page.date}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                // --- Post body ---
                item {
                    val html = page.content
                    if (html.isNullOrBlank()) {


                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight()   // <-- key: fills remaining LazyColumn height
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center

                                ) {
                                Text(
                                    text = "No content available",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                        }
                    } else {
                        PermanentHtmlContent2(
                            html = html,
                            onLinkClicked = stableOnLinkClicked.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(top = 24.dp)
                        )
                    }

                }
            }


        }
    }

}


@Composable
fun postImagePainter(imageUrl: String): Painter {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(Size(1024, 1024))
        .scale(Scale.FILL)
        .placeholder(R.drawable.news_placeholder)
        .error(R.drawable.news_placeholder)
        .build()
    return rememberAsyncImagePainter(request)
}

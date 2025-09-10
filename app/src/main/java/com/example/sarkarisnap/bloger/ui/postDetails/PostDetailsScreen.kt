package com.example.sarkarisnap.bloger.ui.postDetails

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.size.Size
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.FavoriteToggleIcon
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.ChipSize
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.PermanentHtmlContent2
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.PostChip
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.RelatedPostsSection
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.ShareExpandableFab
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.ShareTarget
import com.example.sarkarisnap.core.ui.theme.SandYellow
import com.example.sarkarisnap.core.utils.openUrlInCustomTab
import com.example.sarkarisnap.core.utils.shareViaMessenger
import com.example.sarkarisnap.core.utils.shareViaMore
import com.example.sarkarisnap.core.utils.shareViaTelegram
import com.example.sarkarisnap.core.utils.shareViaWhatsApp
import com.example.sarkarisnap.core.utils.shareViaX
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PostDetailsScreenRoot(
    viewModel: PostDetailsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onOpenPost: (Post) -> Unit,
    onLabelClick: (String) -> Unit
    ) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailsScreen(
        state = state, onAction = { action ->
            when (action) {
                is PostDetailsActions.OnBackClick -> onBackClicked()
                is PostDetailsActions.OnLinkClicked -> { openUrlInCustomTab(context, action.url) }

                is PostDetailsActions.OnRelatedPostClick -> onOpenPost(action.post)
                is PostDetailsActions.OnLabelClick -> onLabelClick(action.label)
                else -> viewModel.onAction(action) // only forward actions that matter
            }
        })
}


@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun PostDetailsScreen(
    state: PostDetailsState,
    onAction: (PostDetailsActions) -> Unit,
) {
    val scrollState = rememberLazyListState()
    var contentHeight by remember { mutableStateOf(0) }
    val stableOnLinkClicked by rememberUpdatedState { url: String ->
        onAction(PostDetailsActions.OnLinkClicked(url))
    }

    val context = LocalContext.current // Added LocalContext

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                Log.d("SCROLL_DEBUG", "Index: $index, Offset: $offset")
                if (index == 0 && offset > 300) {
                    Log.w("SCROLL_WARNING", "⚠️ Scroll jumped unexpectedly to top with offset=$offset")
                }
            }
    }
    Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("Article") },
            navigationIcon = {
                IconButton(onClick = { onAction(PostDetailsActions.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                FavoriteToggleIcon(
                    isFavorite = state.isFavorite,
                    onToggle = { state.post?.let { onAction(PostDetailsActions.OnPostFavoriteClick(it)) } }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SandYellow,
            )
        )
    },
        floatingActionButton = {
            state.post?.let { postToShare ->
                ShareExpandableFab(onShareClick = { target ->
                    val postUrl = postToShare.url
                    val postTitle = postToShare.title
                    when (target) {
                        ShareTarget.WHATSAPP -> shareViaWhatsApp(context, postTitle, postUrl)
                        ShareTarget.TELEGRAM -> shareViaTelegram(context, postTitle, postUrl)
                        ShareTarget.X -> shareViaX(context, postTitle, postUrl)
                        ShareTarget.FACEBOOK -> shareViaMessenger(context, postTitle, postUrl)
                        ShareTarget.MORE -> shareViaMore(context, postTitle, postUrl)
                    }
                })
            }
        },
        floatingActionButtonPosition = FabPosition.End
) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {
        val post = state.post
        if (post == null) {
            // Full-screen loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {

            // --- Hero image(s) ---
            post.imageUrls?.forEach { url ->
                item(key = url) {
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
            }

            // --- Title ---
            item(key = "title_${post.id}") {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            // --- Date ---
            item(key = "date_${post.id}") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Updated: ${post.date}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- Chips / labels ---
            if (post.labels.isNotEmpty()) {
                item(key = "chips_${post.id}") {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    ) {
                        post.labels.forEach { label ->
                            PostChip(
                                size = ChipSize.SMALL,
                                onClick = { onAction(PostDetailsActions.OnLabelClick(label)) },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // --- Post body ---

            item(key = "content_${post.id}") {
                Box(modifier = Modifier.heightIn(min = 1000.dp)) { // Set minimum height
                    PermanentHtmlContent2(
                    html = post.content,
                        onLinkClicked = stableOnLinkClicked,

                        modifier = Modifier.fillMaxWidth()
                )
                }
            }

            // --- Latest Articles ---
            item(key = "latest_${post.id}") {
                Log.d("COMPOSE_DEBUG", "Latest articles rendering for post: ${post.id}")
                RelatedPostsSection(
                    relatedPosts = state.latestArticlesPosts,
                    isLoading = state.isLoadingLatestArticles,
                    title = "Latest articles",
                    onPostClick = { related ->
                        onAction(PostDetailsActions.OnRelatedPostClick(related))
                    },
                )
            }

            item(key = "related_${post.id}") {
                Log.d("COMPOSE_DEBUG", "Latest articles rendering for post: ${post.id}")
                RelatedPostsSection(
                    relatedPosts = state.relatedPosts,
                    isLoading = state.isLoadingRelated,
                    onPostClick = { related ->
                        onAction(PostDetailsActions.OnRelatedPostClick(related))
                    },
                )
            }

        }
    }}
}

@Composable
fun postImagePainter(imageUrl: String): Painter {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(Size(1024, 1024))          // max 1024 px in either dimension
        .scale(Scale.FILL)               // down-scale if larger
        .placeholder(R.drawable.news_placeholder)
        .error(R.drawable.news_placeholder)
        .build()
    return rememberAsyncImagePainter(request)
}
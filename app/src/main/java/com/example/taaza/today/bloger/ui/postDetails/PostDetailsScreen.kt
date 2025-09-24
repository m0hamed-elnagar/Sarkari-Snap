package com.example.taaza.today.bloger.ui.postDetails

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.size.Size
import com.example.taaza.today.R
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.ui.components.FavoriteToggleIcon
import com.example.taaza.today.bloger.ui.components.PostList
import com.example.taaza.today.bloger.ui.postDetails.componentes.AnnotatedHtmlContent
import com.example.taaza.today.bloger.ui.postDetails.componentes.ChipSize
import com.example.taaza.today.bloger.ui.postDetails.componentes.HtmlWebView
import com.example.taaza.today.bloger.ui.postDetails.componentes.HtmlWebView11
import com.example.taaza.today.bloger.ui.postDetails.componentes.PermanentHtmlContent2
import com.example.taaza.today.bloger.ui.postDetails.componentes.PostChip
import com.example.taaza.today.bloger.ui.postDetails.componentes.ShareExpandableFab
import com.example.taaza.today.bloger.ui.postDetails.componentes.ShareTarget
import com.example.taaza.today.bloger.ui.postDetails.componentes.StableHtmlContent
import com.example.taaza.today.bloger.ui.postDetails.componentes.StableHtmlContent3
import com.example.taaza.today.core.ui.theme.SandYellow
import com.example.taaza.today.core.utils.openUrlInCustomTab
import com.example.taaza.today.core.utils.shareViaMessenger
import com.example.taaza.today.core.utils.shareViaMore
import com.example.taaza.today.core.utils.shareViaTelegram
import com.example.taaza.today.core.utils.shareViaWhatsApp
import com.example.taaza.today.core.utils.shareViaX
import org.koin.compose.viewmodel.koinViewModel
import kotlin.compareTo

@Composable
fun PostDetailsScreenRoot(
    viewModel: PostDetailsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onOpenPost: (Post) -> Unit,
    onLabelClick: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val relatedPostsPaging = viewModel.relatedPostsPaged.collectAsLazyPagingItems()
    val latestArticlesPaging = viewModel.latestArticlesPaged.collectAsLazyPagingItems()

    PostDetailsScreen(
        state = state,
        relatedPostsPaging = relatedPostsPaging,
        latestArticlesPaging = latestArticlesPaging,
        onAction = { action ->
            when (action) {
                is PostDetailsActions.OnBackClick -> onBackClicked()
                is PostDetailsActions.OnLinkClicked -> openUrlInCustomTab(context, action.url)
                is PostDetailsActions.OnRelatedPostClick -> onOpenPost(action.post)
                is PostDetailsActions.OnLabelClick -> onLabelClick(action.label)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    state: PostDetailsState,
    relatedPostsPaging: LazyPagingItems<Post>,
    latestArticlesPaging: LazyPagingItems<Post>,
    onAction: (PostDetailsActions) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val stableOnLinkClicked = rememberUpdatedState { url: String ->
        onAction(PostDetailsActions.OnLinkClicked(url))
    }
    val context = LocalContext.current
    LaunchedEffect(scrollState) {
        var lastLogTime = 0L
        snapshotFlow {
            scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val currentTime = System.currentTimeMillis()
            // Only log every 500ms to reduce noise
            if (currentTime - lastLogTime > 500) {
                Log.d("SCROLL_DEBUG", "Index: $index, Offset: $offset")
                lastLogTime = currentTime
            }

            // Only warn about significant unexpected jumps
            if (index == 0 && offset > 500) {
                Log.w(
                    "SCROLL_WARNING",
                    "⚠️ Significant scroll jump detected: offset=$offset"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article") },
                navigationIcon = {
                    IconButton(onClick = { onAction(PostDetailsActions.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FavoriteToggleIcon(
                        isFavorite = state.isFavorite,
                        onToggle = {
                            state.post?.let {
                                onAction(
                                    PostDetailsActions.OnPostFavoriteClick(
                                        it
                                    )
                                )
                            }
                        }
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
    item(key = "main_post_${state.post.id}") {
                    val post = state.post ?: return@item
                    /* everything you already had: hero, title, date, chips, body */
                    PostDetailContent(post = post, onAction = onAction)
                }
                itemsIndexed(
  items = relatedPostsPaging.itemSnapshotList.items.filterNotNull(),
        key = { index, post -> "related_${post.id}_$index" } // Add prefix and index for uniqueness
    ) { index, relatedPost ->

                // visual separator
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        thickness = DividerDefaults.Thickness, color = colorScheme.outlineVariant
                    )

                    // same composable you used for the main post
                PostDetailContent(post = relatedPost, onAction = onAction)
            }
    when (val append = relatedPostsPaging.loadState.append) {
        is LoadState.Loading -> item(key = "loading_footer") { LoadingFooter() }
        is LoadState.Error -> item(key = "error_footer") {
            ErrorFooter(append.error) { relatedPostsPaging.retry() }
        }
        else                 -> Unit
                }

//                // --- Hero image(s) ---
//                items(post.imageUrls.size) { idx ->
//                    val url = post.imageUrls[idx]
//                    val painter = postImagePainter(url)
//                    Image(
//                        painter = painter,
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 12.dp, bottom = 12.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                    )
//                }
//
//                // ----- title -----
//                item {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(12.dp),
//
//                        ) {
//                        Text(
//                            text = post.title,
//                            style = MaterialTheme.typography.headlineSmall
//                        )
//                    }
//                }
//
//                // ----- date -----
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(12.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Updated: ${post.date}",
//                            style = MaterialTheme.typography.labelMedium,
//                            color = colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//                // --- Chips / labels ---
//                if (post.labels.isNotEmpty()) {
//                    item {
//                        FlowRow(
//                            horizontalArrangement = Arrangement.Start,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 12.dp)
//                        ) {
//                            post.labels.forEach { label ->
//                                PostChip(
//                                    size = ChipSize.SMALL,
//                                    onClick = { onAction(PostDetailsActions.OnLabelClick(label)) },
//                                    modifier = Modifier.padding(2.dp)
//                                ) {
//                                    Text(
//                                        text = label.uppercase(),
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//                // --- Post body ---
//                item {
//                    StableHtmlContent3(
//                        html = post.content,
//                        onLinkClicked = stableOnLinkClicked.value,
//                        modifier = Modifier.fillMaxWidth()
//                            .padding(horizontal = 12.dp)
//                            .padding(top = 24.dp)
//                    )
//                }
//
            }
        }
    }
}
@Composable
private fun LazyItemScope.PostDetailContent(
    post: Post,
    onAction: (PostDetailsActions) -> Unit
) {
    // hero images
    post.imageUrls.forEach { url ->
        Image(
            painter = postImagePainter(url),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }

    Text(
        text = post.title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(12.dp)
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Updated: ${post.date}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (post.labels.isNotEmpty()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
        ) {
            post.labels.forEach { label ->
                PostChip(
                    size = ChipSize.SMALL,
                    onClick = { onAction(PostDetailsActions.OnLabelClick(label)) },
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(label.uppercase(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    key("html_${post.id}") { StableHtmlContent(
        html = post.content,
        postId = post.id,
        onLinkClicked = { url -> onAction(PostDetailsActions.OnLinkClicked(url)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .padding(top = 24.dp)
    )}
}
@Composable
private fun SectionWithPaging(
    title: String,
    pagingItems: LazyPagingItems<Post>,
    onPostClick: (Post) -> Unit,
    maxHeight: Dp
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
        ) {
            when {
                pagingItems.loadState.refresh is androidx.paging.LoadState.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )

                pagingItems.itemCount > 0 -> PostList(
                    posts = pagingItems,
                    onPostClick = onPostClick,
                    modifier = Modifier.fillMaxSize(),
                    scrollState = rememberLazyListState()
                )

                pagingItems.loadState.refresh is androidx.paging.LoadState.Error -> Text(
                    "Failed to load posts",
                    Modifier.align(Alignment.Center)
                )

                else -> Text("No posts available", Modifier.align(Alignment.Center))
            }
        }
    }
}
@Composable
private fun LazyItemScope.LoadingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LazyItemScope.ErrorFooter(
    error: Throwable,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error.localizedMessage ?: "Network error",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
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

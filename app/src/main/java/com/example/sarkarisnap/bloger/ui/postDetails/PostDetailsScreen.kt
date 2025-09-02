package com.example.sarkarisnap.bloger.ui.postDetails

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.postImagePainter
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.ChipSize
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.HtmlContent
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.PostChip
import com.example.sarkarisnap.bloger.ui.postDetails.componentes.RelatedPostsSection
import com.example.sarkarisnap.core.ui.theme.LightOrange
import com.example.sarkarisnap.core.ui.theme.SandYellow
import com.example.sarkarisnap.core.utils.openUrlInCustomTab
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PostDetailsScreenRoot(
    viewModel: PostDetailsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onOpenPost: (Post) -> Unit,

    ) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostDetailsScreen(
        state = state, onAction = { action ->
            when (action) {
                is PostDetailsActions.OnBackClick -> onBackClicked()
                is PostDetailsActions.OnLinkClicked -> {

                    Log.d("url", "Opening URL: ${action.url}")

                    openUrlInCustomTab(context, action.url)
                }

                is PostDetailsActions.OnRelatedPostClick -> onOpenPost(action.post)
                else -> viewModel.onAction(action) // only forward actions that matter
            }
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
            TopAppBar(
                title = { Text("Article") },
                navigationIcon = {
                    IconButton(onClick = { onAction(PostDetailsActions.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

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

        // Single LazyColumn for the whole screen – no nesting scrollables
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            .padding(top = 12.dp, bottom = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            // --- Title ---
            item {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }


            // --- Date ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Updated: ${post.date}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    val isFavorite = state.isFavorite
                    IconButton(
                        onClick = { onAction(PostDetailsActions.OnPostFavoriteClick(post)) },
                        modifier = Modifier

                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        LightOrange, Color.Transparent
                                    ),
                                    radius = 70f
                                )
                            )
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            tint = Color.Red,
                            contentDescription = if (isFavorite) {
                                stringResource(R.string.remove_from_favorites)
                            } else {
                                stringResource(R.string.mark_as_favorite)
                            }
                        )
                    }
                }
            }
// --- Chips / labels ---
            if (post.labels.isNotEmpty()) {
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    ) {
                        post.labels.forEach { label ->
                            PostChip(
                                size = ChipSize.SMALL,
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
            item {
                HtmlContent(
                    html = post.content,
                    onLinkClicked = { url ->
                        onAction(PostDetailsActions.OnLinkClicked(url))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {

                RelatedPostsSection(
                    relatedPosts = state.relatedPosts,
                    isLoading = state.isLoadingRelated,
                    title = "Latest articles",
                    onPostClick = { related ->
                        onAction(PostDetailsActions.OnRelatedPostClick(related))
                    },
                )
            }

            item {

                RelatedPostsSection(
                    relatedPosts = state.relatedPosts,
                    isLoading = state.isLoadingRelated,
                    onPostClick = { related ->
                        onAction(PostDetailsActions.OnRelatedPostClick(related))
                    },
                )
            }

        }
    }
}


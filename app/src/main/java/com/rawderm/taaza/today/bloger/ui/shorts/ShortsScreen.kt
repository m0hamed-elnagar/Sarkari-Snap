package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.YouTubeShortsPlayer
import com.rawderm.taaza.today.core.utils.ShareUtils.systemChooser
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun ShortsScreenRoot(
    viewModel: ShortsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {}
) {
    val shortsPaging = viewModel.shorts.collectAsLazyPagingItems()

    ShortsScreen(
        shortsPaging = shortsPaging,
        onAction = { action ->
            when (action) {
                is ShortsActions.OnBackClick -> onBackClicked()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    shortsPaging: LazyPagingItems<Post>,
    onAction: (ShortsActions) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0) { shortsPaging.itemCount }

    /* we only treat a page as settled when paging *and* settle-animation are done */
    var settledPage by remember { mutableIntStateOf(-1) }


    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(100)
            settledPage = pagerState.currentPage
            val next = pagerState.currentPage + 1
            if (next < shortsPaging.itemCount) shortsPaging.retry()
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        beyondViewportPageCount = 3,
        userScrollEnabled = true,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = { pageIndex ->
            val post = shortsPaging.peek(pageIndex)
            "page_${pageIndex}_post_${post?.id ?: "null"}"        }
    ) { pageIndex ->
        val post = shortsPaging[pageIndex] ?: return@VerticalPager
        val videoId = remember(post) { post.videoIds.orEmpty().firstOrNull() }

        ShortsVideoPage(
            post = post,
            videoId = videoId,
            isSelected = pageIndex == settledPage,
            settledPage = settledPage,
            pageIndex = pageIndex,
            onAction = onAction
        )
    }
}

@Composable
private fun ShortsVideoPage(
    post: Post,
    videoId: String?,
    isSelected: Boolean,
    settledPage: Int,
    pageIndex: Int,
    onAction: (ShortsActions) -> Unit
) {
    // Local state for like (you might want to move this to ViewModel)
    var isLiked by remember(post.id) { mutableStateOf(false) }
val context =   LocalContext.current
    val appUrl = context.getString(R.string.app_url)
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(Modifier.fillMaxSize()) {
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )

            /* 1. decide what to draw */
             when {
                videoId.isNullOrBlank() -> PlaceholderBox(stringResource(R.string.no_video))
                abs(pageIndex - settledPage) <= 2 -> {
                    YouTubeShortsPlayer(
                        videoIds = listOf(videoId),
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(9f / 16f),
                        autoPlay = isSelected,   // NEW flag
                        onVideoEnd = { /* optional scroll to next */ }
                    )
                }
                else -> PlaceholderBox(stringResource(R.string.loading_ellipsis))
            }


            /* Bottom black spacer - eats extra height */
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
        }
        /* Text overlay at bottom */
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(post.title, color = Color.White, fontSize = 18.sp,modifier = Modifier.padding(bottom = 8.dp))
            post.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, maxLines = 2)
            }
        }

        val  suffixShare = stringResource(R.string.share_shorts_suffix)
        /* Action buttons on the right side */
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Share button
            IconButton(
                onClick = {
       val postUrl ="$appUrl/shorts/"+ post.rowDate
                    val postTitle = post.title + suffixShare

                    systemChooser(context, postTitle, postUrl)
                },
                modifier = Modifier.size(48.dp).padding(horizontal = 2.dp)
            ) {
                    Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Like button with count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        onAction(ShortsActions.OnPostFavoriteClick(post))
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.like),
                        tint = if (isLiked) Color.White else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.padding(8.dp))

        }

        Text(
            text = "${pageIndex + 1}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun PlaceholderBox(text: String) {
    /* swap with any shimmer / circular spinner you like */
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}
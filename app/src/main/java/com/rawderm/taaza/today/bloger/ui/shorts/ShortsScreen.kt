package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.home.components.YouTubeVideoPlayer
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShortsScreenRoot(
    viewModel: ShortsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {}
) {
    val testPosts = viewModel.testPosts.collectAsLazyPagingItems()
    ShortsScreen(
//        state = state,
        shortsPaging = testPosts,
        onAction = { action ->
            when (action) {
                is ShortsActions.OnBackClick -> onBackClicked()
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
    val pagerState = rememberPagerState(
        initialPage = 0
    ) {
        shortsPaging.itemCount
    }

    // Track when page has settled after scroll
    var settledPage by remember { mutableIntStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            // Wait for any final animations to complete
            delay(100)
            settledPage = pagerState.currentPage
        }
    }

    // Pre-load next pages when we're close to the end
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage + 3 >= shortsPaging.itemCount) {
            shortsPaging.retry()
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        userScrollEnabled = true,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = { pageIndex ->
            shortsPaging.peek(pageIndex)?.id ?: pageIndex
        }
    ) { pageIndex ->
        val post = shortsPaging[pageIndex] ?: return@VerticalPager

        ShortsVideoPage(
            post = post,
            isSelected = pageIndex == settledPage,
            pageIndex = pageIndex
        )
    }
}

@Composable
private fun ShortsVideoPage(
    post: Post,
    isSelected: Boolean,
    pageIndex: Int
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )

            if (!post.videoUrl.isNullOrBlank()) {
                YouTubeVideoPlayer(
                    videoId = post.videoUrl,
                    autoPlay = isSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No video available",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            /* Bottom black spacer - eats extra height */
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
        }

        /* Text overlay at bottom */
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = post.title,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            post.description?.let { description ->
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                }
            }
        }

        /* Optional: Page indicator at top */
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


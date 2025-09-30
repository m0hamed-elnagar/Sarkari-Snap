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
import com.rawderm.taaza.today.bloger.ui.home.components.YouTubeShortsPlayer
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
    val pagerState = rememberPagerState(initialPage = 0) { shortsPaging.itemCount }

    /* we only treat a page as settled when paging *and* settle-animation are done */
    var settledPage by remember { mutableIntStateOf(-1) }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(100)                 // let animation finish
            settledPage = pagerState.currentPage
            /* 3. pre-load next page */
            val next = pagerState.currentPage + 1
            if (next < shortsPaging.itemCount) shortsPaging.retry()
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
        val videoId = remember(post) { post.videoUrl.orEmpty().trim() }

        ShortsVideoPage(
            post = post,
            videoId = videoId,
            isSelected = pageIndex == settledPage,
            pageIndex = pageIndex
        )
    }
}

@Composable
private fun ShortsVideoPage(
    post: Post,
    videoId: String,          // non-null, already extracted
    isSelected: Boolean,
    pageIndex: Int
) {
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
                videoId.isBlank() -> PlaceholderBox("No video")
                else              -> YouTubeShortsPlayer(
                    videoIds = listOf(videoId),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f),
                    onVideoEnd = { /* optional: scroll to next page */ }
                )
            }

            /* Bottom black spacer - eats extra height */
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
        }
        post
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

/* ----------  reusable placeholder  ---------- */
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
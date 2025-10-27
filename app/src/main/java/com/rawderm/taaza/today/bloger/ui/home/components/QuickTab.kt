package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.BannerAd
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.bloger.ui.home.HomeUiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuickTabContentPullRefresh(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { pagedPosts.refresh() }
    )

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        QuickTabContent(
            pagedPosts = pagedPosts,
            onAction = onAction,
            listState = listState
        )

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun QuickTabContent(
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    listState:LazyListState,
) {
    Column(Modifier.fillMaxSize()) {
        // Content area (weight = 1 → banner stays at bottom)
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                pagedPosts.loadState.refresh is LoadState.Loading ->
                    CircularProgressIndicator()

                pagedPosts.itemCount > 0 ->
                    PostFullScreenList(          // <-- full-screen cards
                        posts = pagedPosts,
                        listState = listState,
                        onQuickClick = { postId ->
                            onAction(HomeActions.OnQuickClick(postId))
                        }
                    )

                pagedPosts.loadState.refresh is LoadState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Failed to load posts")
                        Button(onClick = { pagedPosts.refresh() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("No posts available")
                        Button(onClick = { pagedPosts.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        // Ad banner exactly like in trending
        BannerAd(adUnitId = "ca-app-pub-7395572779611582/3592956801")
    }
}
@Composable
fun PostFullScreenList(
    posts: LazyPagingItems<Post>,
    listState: LazyListState = rememberLazyListState(),
    onQuickClick: (String) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            count = posts.itemCount,
            key = { index -> posts[index]?.id ?: index }
        ) { index ->
            val post = posts[index]
            if (post != null) {
  PostFullScreenCard(
                    post = post,
                    modifier = Modifier
                        .fillMaxWidth(),
      onQuickClick = onQuickClick
                )
            }}
        }

}


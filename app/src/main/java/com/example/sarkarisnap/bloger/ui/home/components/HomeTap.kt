package com.example.sarkarisnap.bloger.ui.home.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.PostList
import com.example.sarkarisnap.bloger.ui.home.HomeActions
import com.example.sarkarisnap.bloger.ui.home.HomeUiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeTabWithPullRefresh(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    chipListState: LazyListState,
    listState: LazyListState
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(HomeActions.OnRefresh) }
    )

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        HomeTabContent(
            state, pagedPosts, onAction, listState, chipListState
        )
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun HomeTabContent(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    listState: LazyListState,
    chipsListState: LazyListState
) {
    val selectedLabel = state.selectedLabel
    LaunchedEffect(selectedLabel) {
        Log.d("debug", "HomeTabContent: Label changed to $selectedLabel, scrolling to top")
        listState.animateScrollToItem(0)
        chipsListState.animateScrollToItem(0)
    }
    Column(Modifier.fillMaxSize()) {
        AnimatedChipBar(
            labels = state.labels,
            selectedLabel = state.selectedLabel,
            onLabelSelected = { label ->
                Log.d("debug", "AnimatedChipBar: Label selected $label")
                onAction(HomeActions.OnLabelSelected(label))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            chipsListState = chipsListState
        )

        Box(
            Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                pagedPosts.loadState.refresh is androidx.paging.LoadState.Loading -> CircularProgressIndicator()
                pagedPosts.itemCount > 0 -> PostList(
                    posts = pagedPosts,
                    onPostClick = { onAction(HomeActions.OnPostClick(it)) },
                    modifier = Modifier.fillMaxSize(),
                    scrollState = listState
                )
                pagedPosts.loadState.refresh is androidx.paging.LoadState.Error -> Text("Failed to load posts")
                else -> Text("No posts available")
            }
        }
    }
}

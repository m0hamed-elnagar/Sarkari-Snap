package com.example.taaza.today.bloger.ui.home.components


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.ui.components.PostList
import com.example.taaza.today.bloger.ui.home.HomeActions
import com.example.taaza.today.bloger.ui.home.HomeUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrendingTabContentPullRefresh(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    pages : LazyPagingItems<Page>,
    onAction: (HomeActions) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { pagedPosts.refresh()
            pages.refresh()}
    )

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        TrendingTabContent(
             pagedPosts, onAction, listState,
        )
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun TrendingTabContent(
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    listState: LazyListState,
) {

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                pagedPosts.loadState.refresh is LoadState.Loading ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()), // only here
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }

                pagedPosts.itemCount > 0 -> PostList(
                    posts = pagedPosts,
                    onPostClick = { onAction(HomeActions.OnPostClick(it)) },
                    modifier = Modifier.fillMaxSize(),
                    scrollState = listState
                )

                pagedPosts.loadState.refresh is LoadState.Error ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()), // only here
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Failed to load posts")
                        Button(onClick = { pagedPosts.refresh() }) {
                            Text("Retry")
                        }
                    }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No posts available")
                }
            }
        }
    }
}

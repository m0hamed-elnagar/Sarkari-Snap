package com.example.sarkarisnap.bloger.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.PostList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onPostClick: (Post) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is HomeActions.OnPostClick -> onPostClick(action.post)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit
) {
    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(HomeActions.OnRefresh) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
            PostList(
                posts = state.posts,
                onPostClick = { post -> onAction(HomeActions.OnPostClick(post)) },
                modifier = Modifier.fillMaxSize(),
                scrollState = listState
            )

        // 2. Centered loading indicator (initial load)
        if (state.isLoading && state.posts.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // 3. Pull-to-refresh indicator
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
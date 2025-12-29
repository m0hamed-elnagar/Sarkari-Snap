@file:JvmName("LabeledPostsUiStateKt")

package com.rawderm.taaza.today.bloger.ui.labeled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.PostList
import com.rawderm.taaza.today.core.ui.theme.SandYellow
import org.koin.compose.viewmodel.koinViewModel
@Preview
@Composable
fun LabeledScreenPreview() {
    LabeledScreenRoot(onBackClick = {}, onPostClick = {})
}
@Composable
fun LabeledScreenRoot(
    viewModel: LabeledPostsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onPostClick: (Post) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagedPosts = viewModel.pagedPosts.collectAsLazyPagingItems()
    LabeledScreen(
        state = state,
        pagedPosts = pagedPosts,
        onAction = { action ->
            when (action) {
                is LabeledPostsActions.OnBackClick -> onBackClick()
                is LabeledPostsActions.OnPostClick -> onPostClick(action.post)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LabeledScreen(
    state: LabeledPostsUiState,
    pagedPosts: androidx.paging.compose.LazyPagingItems<Post>,
    onAction: (LabeledPostsActions) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(LabeledPostsActions.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SandYellow,
                )
            )
        }
    ) { padding ->
        val listState = rememberLazyListState()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = { onAction(LabeledPostsActions.OnRefresh) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when {
                pagedPosts.loadState.refresh is androidx.paging.LoadState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                pagedPosts.itemCount > 0 -> PostList(
                    posts = pagedPosts,
                    onPostClick = { post -> onAction(LabeledPostsActions.OnPostClick(post)) },
                    modifier = Modifier.fillMaxSize(),
                    scrollState = listState
                )

                pagedPosts.loadState.refresh is androidx.paging.LoadState.Error -> Text(
                    "Failed to load posts",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> Text("No posts available", modifier = Modifier.align(Alignment.Center))
            }
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
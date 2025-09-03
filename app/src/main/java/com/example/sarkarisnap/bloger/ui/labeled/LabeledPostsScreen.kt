@file:JvmName("LabeledPostsUiStateKt")

package com.example.sarkarisnap.bloger.ui.labeled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.PostList
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsActions
import com.example.sarkarisnap.core.ui.theme.SandYellow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LabeledScreenRoot(
    viewModel: LabeledPostsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onPostClick: (Post) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LabeledScreen(
        state = state,
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
                    )},
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
                // Initial load, no posts yet -> show centered loader only
                state.isLoading && state.posts.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Otherwise show post list
                else -> {
                    PostList(
                        posts = state.posts,
                        onPostClick = { post -> onAction(LabeledPostsActions.OnPostClick(post)) },
                        modifier = Modifier.fillMaxSize(),
                        scrollState = listState
                    )
                }
            }

            // Pull-to-refresh indicator (always shown above)
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
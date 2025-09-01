package com.example.sarkarisnap.bloger.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit
) {
    val postsListState = rememberLazyListState()

    PostList(
        state.posts,
        onPostClick = { post ->
            onAction(HomeActions.OnPostClick(post))},
        modifier = Modifier,
        postsListState
    )
}
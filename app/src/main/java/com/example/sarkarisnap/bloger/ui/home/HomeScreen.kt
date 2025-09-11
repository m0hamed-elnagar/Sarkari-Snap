package com.example.sarkarisnap.bloger.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.PostListStatic
import com.example.sarkarisnap.bloger.ui.home.components.BottomTabRow
import com.example.sarkarisnap.bloger.ui.home.components.HomeTabWithPullRefresh
import com.example.sarkarisnap.core.ui.theme.SandYellow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onPostClick: (Post) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val pagedPosts = viewModel.pagedPosts.collectAsLazyPagingItems()
    HomeScreen(
        state = state, // Pass state argument
        pagedPosts = pagedPosts,
        onAction = { action ->
            when (action) {
                is HomeActions.OnPostClick -> onPostClick(action.post)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
) {
    val title = when (state.selectedTabIndex) {
        0 -> stringResource(R.string.home)
        1 -> stringResource(R.string.favorites)
        else -> ""
    }

    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    val chipListState = remember { LazyListState() }
    // --- Per-label LazyListState map ---
    val labelListStates = remember { mutableMapOf<String, LazyListState>() }
    val currentListState = labelListStates.getOrPut(state.selectedLabel) { LazyListState() }

    LaunchedEffect(state.selectedTabIndex) {
        if (pagerState.currentPage != state.selectedTabIndex)
            pagerState.animateScrollToPage(state.selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        if (state.selectedTabIndex != pagerState.currentPage)
            onAction(HomeActions.OnTabSelected(pagerState.currentPage))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SandYellow)
            )
        },
        bottomBar = { BottomTabRow(state, onAction,pagerState = pagerState,
            scope = scope) }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> HomeTabWithPullRefresh(state, pagedPosts, onAction, chipListState, currentListState)
                1 -> FavoriteTabContent(state, onAction)
            }
        }
    }
}

@Composable
private fun FavoriteTabContent(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
) {
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            state.favoritePosts.isEmpty() -> Text(
                text = "No favorites yet",
                style = MaterialTheme.typography.bodyLarge,
                color  = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            else -> PostListStatic(
                posts = state.favoritePosts,
                onPostClick = { post -> onAction(HomeActions.OnPostClick(post)) },
                modifier = Modifier.fillMaxSize(),
                scrollState = listState
            )
        }
    }
}

@Composable
private fun PagesTabContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pages screen – coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun CategoriesTabContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Categories screen – coming soon", style = MaterialTheme.typography.bodyLarge)
    }
}
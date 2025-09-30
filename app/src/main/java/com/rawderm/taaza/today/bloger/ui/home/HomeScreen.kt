package com.rawderm.taaza.today.bloger.ui.home

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.PostListStatic
import com.rawderm.taaza.today.bloger.ui.home.components.BottomTabRow
import com.rawderm.taaza.today.bloger.ui.home.components.HomeTabWithPullRefresh
import com.rawderm.taaza.today.bloger.ui.home.components.MoreTabScreen
import com.rawderm.taaza.today.bloger.ui.home.components.TrendingTabContentPullRefresh
import com.rawderm.taaza.today.core.ui.theme.SandYellow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onPostClick: (Post) -> Unit,
    onPagesClick: (Page) -> Unit,
    onShortsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val pagedPosts = viewModel.pagedPosts.collectAsLazyPagingItems()
    val trendingPosts = viewModel.trendingPosts.collectAsLazyPagingItems()
    val pages = viewModel.pages.collectAsLazyPagingItems()
    HomeScreen(
        state = state, // Pass state argument
        pagedPosts = pagedPosts,
        trendingPosts = trendingPosts,
        pages= pages,
        onAction = { action ->
            when (action) {
                is HomeActions.OnPostClick -> onPostClick(action.post)
                is HomeActions.OnPageClick -> onPagesClick(action.page)
                is HomeActions.OnShortsClick -> onShortsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}
 fun tabToPager(tabIndex: Int) = if (tabIndex > 2) tabIndex - 1 else tabIndex

/* pager-index -> tab-index */
 fun pagerToTab(pagerIndex: Int) = if (pagerIndex >= 2) pagerIndex + 1 else pagerIndex
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    trendingPosts: LazyPagingItems<Post>,
    pages: LazyPagingItems<Page>,
    onAction: (HomeActions) -> Unit
) {
    val tabs = BottomTab.entries
    val pagerState = rememberPagerState { tabs.size-1 } // ← dynamic count

    val title = when (state.selectedTabIndex) {
        0 -> stringResource(R.string.home)
        1 -> stringResource(R.string.trending)
        3 -> stringResource(R.string.favorites)
        4 -> stringResource(R.string.more)
        else -> ""
    }

    val scope = rememberCoroutineScope()
    val chipListState = remember { LazyListState() }
    // --- Per-label LazyListState map ---
    val labelListStates = remember { mutableMapOf<String, LazyListState>() }
    val currentListState = labelListStates.getOrPut(state.selectedLabel) { LazyListState() }

    val settledPage = pagerState.settledPage
    LaunchedEffect(settledPage) {
        val tabIndex = pagerToTab(pagerState.settledPage)
        if (state.selectedTabIndex != tabIndex) {
            onAction(HomeActions.OnTabSelected(tabIndex))
        }
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
        bottomBar = {
            BottomTabRow(
                modifier = Modifier,
                pagerState = pagerState,
                scope = scope,
                tabs = tabs,
                onShortsClick = { onAction(HomeActions.OnShortsClick) }
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
    when (tabs[pagerToTab(page)]) {          // ← enum instead of int
                BottomTab.HOME-> HomeTabWithPullRefresh(
                    state,
                    pagedPosts,
                    onAction,
                    chipListState,
                    currentListState
                )

                BottomTab.TRENDING -> TrendingTabContentPullRefresh(state,trendingPosts, pages,onAction)
                BottomTab.FAVORITES->FavoriteTabContent(state, onAction)
                BottomTab.MORE -> MoreTabScreen(pages = pages, onAction = onAction)
           else -> {} }
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
enum class BottomTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    HOME(R.string.home, Icons.Default.Home),
    TRENDING(R.string.trending, Icons.AutoMirrored.Filled.TrendingUp),

    // ----- middle gap -----
    EMPTY(R.string.empty, Icons.Default.Add),

    FAVORITES(R.string.favorites, Icons.Default.Favorite),
    MORE(R.string.more, Icons.AutoMirrored.Filled.More)
}
package com.rawderm.taaza.today.bloger.ui.home

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.TopBar
import com.rawderm.taaza.today.bloger.ui.home.components.BottomTab
import com.rawderm.taaza.today.bloger.ui.home.components.BottomTabRow
import com.rawderm.taaza.today.bloger.ui.home.components.HomeTabWithPullRefresh
import com.rawderm.taaza.today.bloger.ui.home.components.LanguagePickerDialog
import com.rawderm.taaza.today.bloger.ui.home.components.MoreTabScreen
import com.rawderm.taaza.today.bloger.ui.home.components.TrendingTabContentPullRefresh
import com.rawderm.taaza.today.bloger.ui.home.components.fav.FavoriteTabContent
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsScreenRoot
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

val tabs = BottomTab.entries

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onPostClick: (Post) -> Unit,
    onPagesClick: (Page) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val pagedPosts = viewModel.pagedPosts.collectAsLazyPagingItems()
    val pagedUiItem = viewModel.pagedUiModels.collectAsLazyPagingItems()
    val trendingPosts = viewModel.trendingPosts.collectAsLazyPagingItems()
    val pages = viewModel.pages.collectAsLazyPagingItems()
    val languageManager: LanguageManager = koinInject()
    val pagerState = rememberPagerState { tabs.size } // ← dynamic count
    val scope = rememberCoroutineScope()
    var showLangDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // run only once
        if (LanguageDataStore(context).isFirstLaunch()) {
            showLangDialog = true
        }
    }
    if (showLangDialog) {
        Box(Modifier
            .fillMaxSize()
            .background(White)) {
            LanguagePickerDialog(
                Modifier
                    .fillMaxSize()
                    .background(White),
                languageManager,
                scope
            ) {}
        }
    } else {
        HomeScreen(
            state = state, // Pass state argument
            pagedPosts = pagedPosts,
            trendingPosts = trendingPosts,
            pages = pages,
            languageManager,
            pagerState,
            scope,
            pagedUiItem = pagedUiItem,
            onAction = { action ->
                when (action) {
                    is HomeActions.OnPostClick -> onPostClick(action.post)
                    is HomeActions.OnPageClick -> onPagesClick(action.page)
                    is HomeActions.OnTabSelected -> { ->
                        if (state.selectedTabIndex != action.index) {
                            scope.launch {
                                pagerState.animateScrollToPage(action.index)
                            }
                        }
                    }

                    else -> Unit
                }
                viewModel.onAction(action)
            })
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    trendingPosts: LazyPagingItems<Post>,
    pages: LazyPagingItems<Page>,
    languageManager: LanguageManager,
    pagerState: PagerState,
    scope: CoroutineScope,
    onAction: (HomeActions) -> Unit,
    pagedUiItem: LazyPagingItems<PostUiItem>
) {

    val chipListState = remember { LazyListState() }
    // --- Per-label LazyListState map ---
    val labelListStates = remember { mutableMapOf<String, LazyListState>() }
    val currentListState = labelListStates.getOrPut(state.selectedLabel) { LazyListState() }
    val shortsViewModel = koinViewModel<ShortsViewModel>()
    val settledPage = pagerState.settledPage

    LaunchedEffect(settledPage) {
        if (state.selectedTabIndex != pagerState.settledPage) {
            onAction(HomeActions.OnTabSelected(pagerState.settledPage))
        }
    }
    LaunchedEffect(state.selectedTabIndex) {
        if (state.selectedTabIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(state.selectedTabIndex)
        }
    }
    Scaffold(
        topBar = {
            AnimatedVisibility(visible = pagerState.currentPage != BottomTab.SHORTS.ordinal) {
                TopBar(languageManager, onAction)
            }
        }, bottomBar = {
            BottomTabRow(
                modifier = Modifier,
                pagerState = pagerState,
                scope = scope,
                tabs = tabs)
        }) { padding ->
        Column(Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(padding)
            ) { page ->
                when (tabs[page]) {          // ← enum instead of int
                    BottomTab.HOME -> HomeTabWithPullRefresh(
                        state,
                        pagedPosts,
                        onAction,
                        chipListState,
                        currentListState,
                        pagedUiItem = pagedUiItem
                    )

                    BottomTab.TRENDING -> TrendingTabContentPullRefresh(
                        state, trendingPosts, pages, onAction
                    )

                    BottomTab.SHORTS -> {
                        ShortsScreenRoot(viewModel = shortsViewModel)
                    }

                    BottomTab.FAVORITES -> FavoriteTabContent(
                        state, onAction, pagerState, shortsViewModel)
                    BottomTab.MORE -> MoreTabScreen(pages = pages, onAction = onAction)
                }
            }
        }
    }
}


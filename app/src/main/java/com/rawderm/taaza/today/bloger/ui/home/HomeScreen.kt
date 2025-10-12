package com.rawderm.taaza.today.bloger.ui.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.PostListStatic
import com.rawderm.taaza.today.bloger.ui.home.components.BottomTabRow
import com.rawderm.taaza.today.bloger.ui.home.components.HomeTabWithPullRefresh
import com.rawderm.taaza.today.bloger.ui.home.components.MoreTabScreen
import com.rawderm.taaza.today.bloger.ui.home.components.TrendingTabContentPullRefresh
import com.rawderm.taaza.today.bloger.ui.home.components.fav.FavoriteVideosScreen
import com.rawderm.taaza.today.core.ui.theme.SandYellow
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
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
    val languageManager: LanguageManager = koinInject()

    HomeScreen(
        state = state, // Pass state argument
        pagedPosts = pagedPosts,
        trendingPosts = trendingPosts,
        pages = pages,
        languageManager,
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
    languageManager: LanguageManager,
    onAction: (HomeActions) -> Unit
) {
    val tabs = BottomTab.entries
    val pagerState = rememberPagerState { tabs.size - 1 } // ← dynamic count

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
    val context = LocalContext.current
    val locale = remember { Lingver.getInstance().getLocale().language }
    Scaffold(

        topBar = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
//                    .background(Gray)
//                    .padding(bottom = .5.dp)
            ) {
                TopAppBar(
                    navigationIcon = {          // <- leading icon
                        Image(
                            painter = painterResource(id = R.drawable.icon2), // or R.drawable.ic_logo
                            contentDescription = "App logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(80.dp)
                                .aspectRatio(16f / 9f)  // landscape box
                                .padding(start = (0).dp)

                        )
                    },

                    title = {
                    },
                    actions = {
                        // Language switcher dropdown
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            // Current language display
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { expanded = true }
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (locale == "en") "EN" else "HI",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change Language",
                                    tint = Color.Black
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("English")
                                            if (locale == "en") {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected"
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (locale == "en") {
                                            expanded = false
                                            return@DropdownMenuItem
                                        }
                                        onAction(HomeActions.OnLoading)
                                        Log.d("LANG", "changeLanguage() invoked: en")
                                        languageManager.setLanguageAndRestart("en", context)

                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("हिन्दी")
                                            if (locale == "hi") {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected"
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (locale == "hi") {
                                            expanded = false
                                            return@DropdownMenuItem
                                        }


                                        // Restart the app to ensure language change is applied everywhere
                                        Log.d("LANG", "changeLanguage() invoked: hi")
                                        languageManager.setLanguageAndRestart("hi", context)

                                        expanded = false
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = White),

                    )
            }
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
                BottomTab.HOME -> HomeTabWithPullRefresh(
                    state,
                    pagedPosts,
                    onAction,
                    chipListState,
                    currentListState
                )

                BottomTab.TRENDING -> TrendingTabContentPullRefresh(
                    state,
                    trendingPosts,
                    pages,
                    onAction
                )

                BottomTab.FAVORITES -> FavoriteTabContent(state, onAction)
                BottomTab.MORE -> MoreTabScreen(pages = pages, onAction = onAction)
                else -> {}
            }
        }


    }
}


@Composable
private fun FavoriteTabContent(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()
     .background(White)
     ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = White,
            contentColor = Black
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("Videos") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("Posts") }
            )
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false             // ← disables swipe
        ) { page ->
            when (page) {
                0 -> FavoriteVideosScreen(
                    shorts = state.favoriteShorts,
                    onVideoClick = {}
                )

                1 -> {
                    val listState = rememberLazyListState()
                    Box(
                        modifier = Modifier.fillMaxSize()
                     .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            state.favoritePosts.isEmpty() -> Text(
                                text = stringResource(R.string.no_favorites_yet),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            else -> PostListStatic(
                                posts = state.favoritePosts,
                                onPostClick = { post ->
                                    onAction(HomeActions.OnPostClick(post))
                                },
                                modifier = Modifier.fillMaxSize(),
                                scrollState = listState
                            )
                        }
                    }
                }
            }
        }
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
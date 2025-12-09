package com.rawderm.taaza.today.bloger.ui.quiks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.ui.components.ads.NativeScreen
import com.rawderm.taaza.today.bloger.ui.quiks.components.PostFullScreenCard
import com.rawderm.taaza.today.bloger.ui.quiks.components.QuikFullScreen
import org.koin.androidx.compose.koinViewModel


@Composable
fun QuikScreenRoot(
    viewModel: QuiksViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onQuickClick: (postID: String) -> Unit,
    onPageMenuClick: (pageId: String) -> Unit = {}

) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val quickPosts = viewModel.uiQuiks.collectAsLazyPagingItems()
    val pages = viewModel.pages.collectAsLazyPagingItems()

    BackHandler { onBackClicked() }
    QuikTabContentPullRefresh(
        state = state,
        pagedPosts = quickPosts,
        pages = pages,
        onAction = { action ->
            when (action) {
                is QuiksActions.OnQuickClick -> {
                    onQuickClick(action.postId)
                }

                is QuiksActions.OnPageClick -> {
                    onPageMenuClick(action.page.id)
                }

                else -> viewModel.onAction(action)
            }
        })

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuikTabContentPullRefresh(
    state: QuiksState,
    pagedPosts: LazyPagingItems<QuikUiItem>,
    pages: LazyPagingItems<Page>, // 👈 Add this
    onAction: (QuiksActions) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = {
            onAction(QuiksActions.OnRefresh)
            pagedPosts.refresh()
        }
    )

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiks") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {

                        repeat(pages.itemCount) { index ->
                            val page = pages[index] ?: return@repeat
                            DropdownMenuItem(
                                text = { Text(page.title) },
                                onClick = {
                                    menuExpanded = false
                                    onAction(QuiksActions.OnPageClick(page))
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            QuikTabContent(
                pagedPosts = pagedPosts,
                onAction = onAction,
                listState = listState
            )

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun QuikTabContent(
    pagedPosts: LazyPagingItems<QuikUiItem>,
    onAction: (QuiksActions) -> Unit,
    listState: LazyListState,
) {
    Column(Modifier.fillMaxSize()) {
        // Content area (weight = 1 → banner stays at bottom)
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                pagedPosts.loadState.refresh is LoadState.Loading ->
                    CircularProgressIndicator()

                pagedPosts.itemCount > 0 ->
                    PostFullScreenList(          // <-- full-screen cards
                        posts = pagedPosts,
                        listState = listState,
                        onQuickClick = { postId ->
                            onAction(QuiksActions.OnQuickClick(postId))
                        }
                    )

                pagedPosts.loadState.refresh is LoadState.Error -> {
                    FullScreenMessage(
                        text = stringResource(R.string.something_went_wrong),
                        onRetry = { pagedPosts.refresh() }
                    )
                }

                else -> {
                    FullScreenMessage(
                        text = stringResource(R.string.no_posts_available),
                        onRetry = { pagedPosts.refresh() }
                    )
                }
            }
        }

    }
}

@Composable
fun PostFullScreenList(
    posts: LazyPagingItems<QuikUiItem>,
    listState: LazyListState = rememberLazyListState(),
    onQuickClick: (String) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            count = posts.itemCount,
            key = { index ->
                val quikItem = posts.peek(index)
                when {
                    quikItem?.isAd == true -> "ad_${quikItem.adId ?: index}"
                    else -> "post_${quikItem!!.quik!!.id}"

                }
            }
        ) { index ->
            val post = posts[index] ?: return@items
            var isAdLoaded by remember { mutableStateOf(false) }
            val targetHeight = if (isAdLoaded) 400.dp else 2.dp // tiny but non-zero
            val targetAlpha = if (isAdLoaded) 1f else 0f

            if (post.isAd) {
                NativeScreen(
                    nativeAdUnitID = "ca-app-pub-7395572779611582/5077711672",
                    modifier = Modifier
                        .height(targetHeight)
                        .graphicsLayer { alpha = targetAlpha } // hide while tiny
                ) { loaded ->
                    isAdLoaded = loaded
                }
            } else
                QuikFullScreen(
                    post = post.quik!!,
                    modifier = Modifier
                        .fillMaxWidth()
                )
        }
    }
}

@Composable
private fun FullScreenMessage(text: String, onRetry: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}
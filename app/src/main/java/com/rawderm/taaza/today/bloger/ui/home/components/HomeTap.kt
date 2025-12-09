package com.rawderm.taaza.today.bloger.ui.home.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.components.PostListWithAds
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.bloger.ui.home.HomeUiState
import com.rawderm.taaza.today.bloger.ui.home.PostUiItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeTabWithPullRefresh(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    chipListState: LazyListState,
    listState: LazyListState,
    pagedUiItem: LazyPagingItems<PostUiItem>
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = {
            onAction(HomeActions.OnRefresh)
            pagedPosts.refresh()
            pagedUiItem.refresh()
        }
    )

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        HomeTabContent(state, pagedPosts, onAction, listState, chipListState, pagedUiItem)
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state      = pullRefreshState,
            modifier   = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun HomeTabContent(
    state: HomeUiState,
    pagedPosts: LazyPagingItems<Post>,
    onAction: (HomeActions) -> Unit,
    listState: LazyListState,
    chipsListState: LazyListState,
    pagedUiItem: LazyPagingItems<PostUiItem>
) {
    val showLoadingInsteadOfEmpty = remember { mutableStateOf(true) }
    LaunchedEffect(pagedPosts.itemCount) {
        if (pagedPosts.itemCount == 0) {
            delay(5_000)                 // wait 5s
            showLoadingInsteadOfEmpty.value = false         // now allow “No posts” to appear
        }
    }
    Column(Modifier.fillMaxSize()) {
        AnimatedChipBar(
            labels          = state.labels,
            selectedLabel   = state.selectedLabel,
            onLabelSelected = { label ->
                Log.d("debug", "AnimatedChipBar: Label selected $label")
                onAction(HomeActions.OnLabelSelected(label))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            chipsListState = chipsListState
        )


        when {
            pagedUiItem.loadState.refresh is LoadState.Loading-> FullScreenProgress()
            pagedPosts.loadState.refresh is LoadState.Error   -> FullScreenError(onRetry = {
                onAction(HomeActions.OnRefresh)
                pagedPosts.refresh()
            })

            pagedUiItem.itemCount > 0 -> PostListWithAds(
                pagedUiItem = pagedUiItem,
                onPostClick = { onAction(HomeActions.OnPostClick(it)) },
                modifier    = Modifier.fillMaxSize(),
                scrollState = listState
            )
            else -> FullScreenEmpty()
        }
    }
}

@Composable
private fun FullScreenProgress() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun FullScreenError(onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.failed_to_load_posts))
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun FullScreenEmpty() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text  = stringResource(R.string.no_posts_available),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
package com.example.sarkarisnap.bloger.ui.home.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sarkarisnap.bloger.ui.components.PostList
import com.example.sarkarisnap.bloger.ui.home.HomeActions
import com.example.sarkarisnap.bloger.ui.home.HomeUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeTabWithPullRefresh(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
    listState: LazyListState,
    chipListState: LazyListState
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(HomeActions.OnRefresh) }
    )

//    /* auto-load – unchanged */
//LaunchedEffect(listState, state.selectedLabel) {
//    snapshotFlow {
//        val info = listState.layoutInfo
//        val total = info.totalItemsCount
//        val last = info.visibleItemsInfo.lastOrNull()
//        val condition = total > 0 &&
//                last != null &&
//                last.index >= total - 3 &&
//                !state.isLoadingMore &&
//                !state.isRefreshing
//        Log.d("PAGINATION_DEBUG", "total=$total, lastIndex=${last?.index}, condition=$condition")
//        condition
//    }
//    .distinctUntilChanged()
//    .filter { it }
//    .collect {
//        Log.d("PAGINATION_TRIGGER", "label=${state.selectedLabel}  posts=${state.posts.size}")
//        onAction(HomeActions.OnNextPage)
//    }
//}
    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        HomeTabContent(
            state, onAction, listState,  chipListState
        )

        if (state.isLoadingMore) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun HomeTabContent(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
    listState: LazyListState,
    chipsListState: LazyListState
) {

    val selectedLabel = state.selectedLabel
    LaunchedEffect(selectedLabel) {
        listState.animateScrollToItem(0)
        chipsListState.animateScrollToItem(0)
    }

    LaunchedEffect(listState,selectedLabel) {
        snapshotFlow {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            val last = info.visibleItemsInfo.lastOrNull()
            val condition = total > 0 &&
                    last != null &&
                    last.index >= total - 3 &&
                    !state.isLoadingMore &&
                    !state.isRefreshing
            Log.d("PAGINATION_DEBUG", "total=$total, lastIndex=${last?.index}, condition=$condition")
            condition
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                Log.d("PAGINATION_TRIGGER", "label=${state.selectedLabel}  posts=${state.posts.size}")
                onAction(HomeActions.OnNextPage)
            }
    }
    Column(Modifier.fillMaxSize()) {
        AnimatedChipBar(
            labels = state.labels,
            selectedLabel = selectedLabel,
            onLabelSelected = { label ->
                onAction(HomeActions.OnLabelSelected(label))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            chipsListState = chipsListState
        )

        Box(
            Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.posts.isNotEmpty() -> PostList(
                    posts = state.posts,
                    onPostClick = { onAction(HomeActions.OnPostClick(it)) },
                    modifier = Modifier.fillMaxSize(),
                    scrollState = listState
                )

                else -> Text("No posts available")
            }
        }
    }
}

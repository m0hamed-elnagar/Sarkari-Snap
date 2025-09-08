package com.example.sarkarisnap.bloger.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sarkarisnap.bloger.ui.components.PostList
import com.example.sarkarisnap.bloger.ui.home.HomeActions
import com.example.sarkarisnap.bloger.ui.home.HomeUiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeTabWithPullRefresh(
state: HomeUiState,
onAction: (HomeActions) -> Unit
) {
val listState = rememberLazyListState()
val pullRefreshState = rememberPullRefreshState(
    refreshing = state.isRefreshing,
    onRefresh = { onAction(HomeActions.OnRefresh) }
)

Box(
    Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
) {
    HomeTabContent(state, onAction, listState)

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
listState: LazyListState
) {
var selectedIndex by remember { mutableIntStateOf(0) }   // 0 = "All"

val selectedLabel = state.labels.getOrNull(selectedIndex) ?: "All"

Column(Modifier.fillMaxSize()) {

    AnimatedChipBar(
        labels = state.labels,
        selectedLabel = selectedLabel,
        onLabelSelected = { label ->
            selectedIndex = state.labels.indexOf(label)
            onAction(HomeActions.OnLabelSelected(label))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )

    Box(
        Modifier
            .fillMaxSize()
            .weight(1f), // ✅ now inside ColumnScope
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading && state.posts.isEmpty() -> CircularProgressIndicator()
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

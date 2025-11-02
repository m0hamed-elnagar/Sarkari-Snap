package com.rawderm.taaza.today.bloger.ui.home.components.fav

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Short
import com.rawderm.taaza.today.bloger.ui.components.BannerAd
import com.rawderm.taaza.today.bloger.ui.components.PostListStatic
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.bloger.ui.home.HomeUiState
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsActions
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
 fun FavoriteTabContent(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
    onBackClicked: () -> Unit = {},
    //todo add Callback
    shortsVM: ShortsViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    BackHandler { onBackClicked()}

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = { Text(stringResource(R.string.articles)) }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text(stringResource(R.string.videos)) }
            )
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = false             // ← disables swipe
        ) { page ->
            when (page) {
                1 -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Videos content takes most space
                        FavoriteVideosScreen(
                            shorts = state.favoriteShorts,
                            onVideoClick = { date ->
                                scope.launch {
                                    onAction(HomeActions.OnTabSelected(2))
                                }
                                shortsVM.onAction(ShortsActions.OnGetShortsByDate(date))
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // Banner at bottom of videos tab
                        BannerAd(
                            adUnitId = "ca-app-pub-7395572779611582/3592956801",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }
                }

                0 -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val listState = rememberLazyListState()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                state.favoritePosts.isEmpty() -> Text(
                                    text = stringResource(R.string.no_favorites_yet),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                else -> {
                                    PostListStatic(
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
                        BannerAd(
                            adUnitId = "ca-app-pub-7395572779611582/3592956801",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )

                    }
                }
            }
        }
    }


}

@Composable
fun FavoriteVideosScreen(
    shorts: List<Short>,
    onVideoClick: (date: String) -> Unit = {},
    modifier: Modifier = Modifier
) {    if (shorts.isEmpty()) {
        // Show empty state when no favorite videos
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_favorite_videos),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.videos_you_save_will_appear_here),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(shorts, key = { it.id }) { short ->
                ShortThumb(
                    short = short,
                    onClick = { onVideoClick(short.rowDate) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
@Composable
private fun ShortThumb(
    short: Short,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    AsyncImage(
        model = short.thumbUrl(),
        contentDescription = short.title,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .aspectRatio(9f / 16f)
    )
    Log.d("updatedat ", "ShortThumb: "+ short.updatedAt)
    Text(
        text = short.updatedAt,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(top = 4.dp)
    )
}
fun Short.thumbUrl(): String =
    "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
// -----------------  Thumb composable -----------------

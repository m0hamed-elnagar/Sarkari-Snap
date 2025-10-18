package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.ui.components.BannerAd
import com.rawderm.taaza.today.bloger.ui.components.YouTubeShortsPlayer
import com.rawderm.taaza.today.bloger.ui.components.ads.NativeScreen
import com.rawderm.taaza.today.core.utils.ShareUtils.systemChooser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun ShortsScreenRoot(
    viewModel: ShortsViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {}
) {
    val rawFlow = viewModel.uiShorts          // Flow<PagingData<ShortUiItem>>
    val failedAdIds = remember { mutableStateSetOf<String>() }

    /* 1. filter the FLOW (not the LazyPagingItems) */
    val filteredFlow = remember(rawFlow, failedAdIds) {
        rawFlow.map { pagingData ->
            pagingData.filter { item ->
                if (item.isAd) {
                    // Check if this ad has failed to load
                    item.adId !in failedAdIds
                } else {
                    true
                }
            }
        }
    }

    /* 2. collect the already-filtered flow */
    val shortsPaging = filteredFlow.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(initialPage = 0) { shortsPaging.itemCount }
    LaunchedEffect(Unit) {
        viewModel.scrollToTop.collectLatest {
            if (shortsPaging.itemCount > 0) {
                pagerState.scrollToPage(0)
                viewModel.onScrollToTopConsumed() // acknowledge → sticky reset
            }
        }
    }


    ShortsScreen(
        shortsPaging = shortsPaging,
        pagerState = pagerState,
        failedAdIds = failedAdIds,        // pass the failed ad IDs set
        onAction = { action ->
            when (action) {
                is ShortsActions.OnBackClick -> onBackClicked()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    shortsPaging: LazyPagingItems<ShortUiItem>,
    pagerState: PagerState,
    failedAdIds: MutableSet<String>, // Receive the failed ad IDs set
    onAction: (ShortsActions) -> Unit,
) {

    /* we only treat a page as settled when paging *and* settle-animation are done */
    var settledPage by remember { mutableIntStateOf(-1) }


    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            delay(100)
            settledPage = pagerState.currentPage
            val next = pagerState.currentPage + 1
            if (next < shortsPaging.itemCount) shortsPaging.retry()
        }
    }
    val showLoadingInsteadOfEmpty = remember { mutableStateOf(true) }
    LaunchedEffect(shortsPaging.itemCount) {
        if (shortsPaging.itemCount == 0) {
            showLoadingInsteadOfEmpty.value = true          // reset in case we come back to 0
            delay(5_000)                 // wait 5s
            showLoadingInsteadOfEmpty.value = false         // now allow "No posts" to appear
        }
    }
    /* 1. empty state */
    if (shortsPaging.itemCount == 0) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (showLoadingInsteadOfEmpty.value) {
                CircularProgressIndicator()
            } else {

                Text(
                    text = stringResource(R.string.no_shorts_yet),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
        return   // do not draw the pager at all
    }
    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        beyondViewportPageCount = 3,
        userScrollEnabled = true,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = { pageIndex ->
            val shortItem = shortsPaging.peek(pageIndex)
            when {
                shortItem?.isAd == true -> "ad_${shortItem.adId ?: pageIndex}"
                else                    -> "page_${pageIndex}_post_${shortItem?.short?.id ?: "null"}"
            }
        }

    ) { pageIndex ->
        val shortItem = shortsPaging[pageIndex] ?: return@VerticalPager
        if (shortItem.isAd) {
            val adKey = shortItem.adId
            NativeScreen(
                onAdResult = { loaded ->
                    // If ad failed to load, add its ID to the failed set
//                    adKey?.let {
//                        if (!loaded && adKey !in failedAdIds) {
//                       failedAdIds +=adKey
//                        shortsPaging.refresh()    // drop the row – no scroll, no loop
//                    }
//                    }

                }
            )
            return@VerticalPager

        }
        val videoId = remember(shortItem) { shortItem.short?.videoId }

        ShortsVideoPage(
            shortItem = shortItem,
            videoId = videoId,
            isSelected = pageIndex == settledPage,
            settledPage = settledPage,
            pageIndex = pageIndex,
            onAction = onAction
        )
    }
}

@Composable
private fun ShortsVideoPage(
    shortItem: ShortUiItem,
    videoId: String?,
    isSelected: Boolean,
    settledPage: Int,
    pageIndex: Int,
    onAction: (ShortsActions) -> Unit
) {
    // Local state for like (you might want to move this to ViewModel)
    val isLiked = shortItem.isFavorite
    val context = LocalContext.current
    val appUrl = context.getString(R.string.app_url)
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(Modifier.fillMaxSize()) {
            BannerAd()
            /* 1. decide what to draw */
            when {
                videoId.isNullOrBlank() -> PlaceholderBox(stringResource(R.string.no_video))
                abs(pageIndex - settledPage) <= 2 -> {
                    YouTubeShortsPlayer(
                        videoIds = listOf(videoId),
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(9f / 16f),
                        autoPlay = isSelected,   // NEW flag
                        onVideoEnd = { /* optional scroll to next */ }
                    )
                }

                else -> PlaceholderBox(stringResource(R.string.loading_ellipsis))
            }


        }
//        /* Text overlay at bottom */
//        Column(
//            Modifier
//                .align(Alignment.BottomStart)
//                .padding(16.dp)
//        ) {
//            shortItem.short?.title?.let {
//                Text(
//                    it,
//                    color = Color.White,
//                    fontSize = 18.sp,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//            }
//        }

        val suffixShare = stringResource(R.string.share_shorts_suffix)
        /* Action buttons on the right side */
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Share button
            IconButton(
                onClick = {
                    val postUrl = "$appUrl/shorts/" + shortItem.short?.rowDate
                    val postTitle = shortItem.short?.title + suffixShare

                    systemChooser(context, postTitle, postUrl)
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(horizontal = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // Like button with count
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        onAction(ShortsActions.OnPostFavoriteClick(shortItem))
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.like),
                        tint = if (isLiked) Color.White else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.padding(8.dp))

        }


    }
}

@Composable
private fun PlaceholderBox(text: String) {
    /* swap with any shimmer / circular spinner you like */
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}
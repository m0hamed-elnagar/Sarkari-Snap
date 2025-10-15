package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
private fun BottomTabRowPreview() {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState { 3 }
    BottomTabRow(pagerState = pagerState, scope = rememberCoroutineScope())
}
enum class BottomTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    HOME(R.string.home, Icons.Default.Home),
    TRENDING(R.string.trending, Icons.AutoMirrored.Filled.TrendingUp),

    // ----- middle gap -----
    SHORTS(R.string.empty, Icons.Default.Add),

    FAVORITES(R.string.favorites, Icons.Default.Favorite),
    MORE(R.string.more, Icons.AutoMirrored.Filled.More)
}

@Composable
fun BottomTabRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    scope: CoroutineScope,
    tabs: List<BottomTab> = BottomTab.entries,
) {
    val barBackground = White
    val selectedColor = Black
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Gray)
            .padding(top = .5.dp)
            .background(barBackground)
            .padding(top = 4.dp)
            .background(Black)
            .navigationBarsPadding(), contentAlignment = Alignment.TopCenter
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = barBackground,
            indicator = { positions ->
                val idx = pagerState.currentPage
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(positions[idx]),
                    height = 3.dp,
                    color = selectedColor
                )
            }) {
            tabs.forEachIndexed { index, tab ->
                val selected = pagerState.currentPage == index

                when (tab) {
                    BottomTab.SHORTS -> Tab(
                        selected = selected,
                        onClick = { scope.launch { pagerState.scrollToPage(index) } }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = modifier
                                .offset(y = (-2).dp)
                                .size(56.dp)
                        ) {
                            Icon(
                                painter =if (selected){painterResource(R.drawable.shorts_btn)}else painterResource(R.drawable.shorts_selected),
                                contentDescription = stringResource(R.string.create),
                                tint = if (selected) Unspecified else Gray,
                                modifier = Modifier
                                    .size(56.dp)
                           )
                        }
                    }

                    else -> {
                        Tab(
                            selected = selected,
                            onClick = { scope.launch { pagerState.scrollToPage(index) } }) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    painter = rememberVectorPainter(tab.icon),
                                    contentDescription = null,
                                    tint = if (selected) selectedColor else unselectedColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = stringResource(tab.labelRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) selectedColor else unselectedColor
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}
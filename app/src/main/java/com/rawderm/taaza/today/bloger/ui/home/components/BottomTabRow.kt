package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.ui.home.BottomTab
import com.rawderm.taaza.today.bloger.ui.home.pagerToTab
import com.rawderm.taaza.today.bloger.ui.home.tabToPager
import com.rawderm.taaza.today.core.ui.theme.LightOrange
import com.rawderm.taaza.today.core.ui.theme.SandYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
private fun BottomTabRowPreview() {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState { 3 }
    BottomTabRow(pagerState = pagerState, scope = rememberCoroutineScope())
}

@Composable
fun BottomTabRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    scope: CoroutineScope,
    tabs: List<BottomTab> = BottomTab.entries,
    onShortsClick: () -> Unit = {}
) {
    val barBackground = SandYellow
    val selectedColor = LightOrange
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(barBackground)
            .padding(top = 8.dp)
            .background(Color.Black)
            .navigationBarsPadding(), contentAlignment = Alignment.TopCenter
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage.let(::pagerToTab),
            containerColor = barBackground,
            indicator = { positions ->
                val idx = pagerState.currentPage.let(::pagerToTab)
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(positions[idx]),
                    height = 3.dp,
                    color = selectedColor
                )
            }) {
            tabs.forEachIndexed { index, tab ->
                when (tab) {
                    BottomTab.EMPTY -> Tab(
                        selected = false,
                        onClick = { },
                        enabled = false,
                        content = { Box(Modifier.size(56.dp)) })

                    else -> {
                        val selected = pagerState.currentPage == tabToPager(index)
                        Tab(
                            selected = selected,
                            onClick = { scope.launch { pagerState.scrollToPage(tabToPager(index)) } }) {
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .offset(y = (-2).dp)          // slightly higher
                .size(56.dp)                   // bigger
        ) {
            Icon(
                painter = painterResource(R.drawable.shorts_btn),
                contentDescription = stringResource(R.string.create),
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onShortsClick() })
        }
    }
}
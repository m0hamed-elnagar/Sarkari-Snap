package com.example.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taaza.today.R
import com.example.taaza.today.bloger.ui.home.TAB_COUNT
import com.example.taaza.today.core.ui.theme.LightOrange
import com.example.taaza.today.core.ui.theme.SandYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomTabRow(
    pagerState: PagerState,
    scope: CoroutineScope
) {
    val barBackground = SandYellow
    val selectedColor = LightOrange
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)

    TabRow(
        selectedTabIndex = pagerState.currentPage,              // ← single source
        containerColor   = barBackground,
        indicator = { positions ->
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(positions[pagerState.currentPage]), // ← same source
                height = 3.dp,
                color  = selectedColor
            )
        }
    ) {
        val labels = listOf(
            stringResource(R.string.home),
            stringResource(R.string.trending),
            stringResource(R.string.favorites),
            stringResource(R.string.more)
        )
        val icons = listOf(
            rememberVectorPainter(Icons.Default.Home),
            painterResource(R.drawable.ic_trending),
            rememberVectorPainter(Icons.Default.Favorite),
            rememberVectorPainter(Icons.AutoMirrored.Filled.More)
        )

        repeat(TAB_COUNT) { index ->
            val selected = pagerState.currentPage == index
            Tab(
                selected = selected,
                onClick  = { scope.launch { pagerState.scrollToPage(index) } },
                content  = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(Modifier.size(24.dp).wrapContentSize(Alignment.Center)) {
                            Icon(
                                painter = icons[index],
                                contentDescription = null,
                                tint = if (selected) selectedColor else unselectedColor
                            )
                        }
                        Text(
                            text  = labels[index],
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) selectedColor else unselectedColor
                        )
                    }
                }
            )
        }
    }
}
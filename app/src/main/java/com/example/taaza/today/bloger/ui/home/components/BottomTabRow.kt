package com.example.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taaza.today.bloger.ui.home.BottomTab
import com.example.taaza.today.core.ui.theme.LightOrange
import com.example.taaza.today.core.ui.theme.SandYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomTabRow(
    pagerState: PagerState,
    scope: CoroutineScope,
    tabs: List<BottomTab> = BottomTab.entries
) {
    val barBackground = SandYellow
    val selectedColor = LightOrange
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)

    TabRow(
        selectedTabIndex = pagerState.currentPage.coerceIn(0, tabs.lastIndex),
        containerColor = barBackground,
        indicator = { positions ->
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(positions[pagerState.currentPage.coerceIn(0, tabs.lastIndex)]),
                height = 3.dp,
                color = selectedColor
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = pagerState.currentPage == index
            Tab(
                selected = selected,
                onClick = { scope.launch { pagerState.scrollToPage(index) } },
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
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
            )
        }
    }
}
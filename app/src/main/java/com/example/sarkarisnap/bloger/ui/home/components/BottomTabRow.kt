package com.example.sarkarisnap.bloger.ui.home.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.ui.home.HomeActions
import com.example.sarkarisnap.bloger.ui.home.HomeUiState
import com.example.sarkarisnap.core.ui.theme.LightOrange
import com.example.sarkarisnap.core.ui.theme.SandYellow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun BottomTabRow(
    state: HomeUiState,
    onAction: (HomeActions) -> Unit,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    val barBackgroundColor = SandYellow
    val selectedColor = LightOrange
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    TabRow(
        selectedTabIndex = state.selectedTabIndex,
        containerColor = barBackgroundColor,
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTabIndex]),
                height = 3.dp,
                color = selectedColor
            )
        }
    ) {
        Tab(
            selected = state.selectedTabIndex == 0,
            onClick = {
                onAction(HomeActions.OnTabSelected(0))
                scope.launch { pagerState.animateScrollToPage(0) }   // ← animate
            }, icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (state.selectedTabIndex == 0) selectedColor else unselectedColor
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.home),
                    color = if (state.selectedTabIndex == 0) selectedColor else unselectedColor
                )
            }
        )

        Tab(
            selected = state.selectedTabIndex == 1,
            onClick = {
                onAction(HomeActions.OnTabSelected(1))
                scope.launch { pagerState.animateScrollToPage(1) }
            },
            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorites",
                    tint = if (state.selectedTabIndex == 1) selectedColor else unselectedColor
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.favorites),
                    color = if (state.selectedTabIndex == 1) selectedColor else unselectedColor
                )
            }
        )



    }
}

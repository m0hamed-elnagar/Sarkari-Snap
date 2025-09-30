package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
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

@Preview(showBackground = true, backgroundColor = 0x00000000) // same colour as bar
@Composable
fun BottomTabRowPreview() {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()

    BottomTabRow(
        pagerState = pagerState,
        scope = scope
    )
}@Composable
fun BottomTabRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    scope: CoroutineScope,
    tabs: List<BottomTab> = BottomTab.entries,
    onShortsClick: () -> Unit = {}          // ← new lambda
) {
    val barBackground = SandYellow
    val selectedColor = LightOrange
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(barBackground)
            .padding(top = 12.dp)
            .background(Color.Black)
            .navigationBarsPadding()

        ,
        contentAlignment = Alignment.TopCenter
    ) {
        /* --------- draw only 4 real tabs --------- */
        TabRow(
            selectedTabIndex = pagerState.currentPage.let(::pagerToTab),
            containerColor = barBackground,
            indicator = { positions ->
                val idx = pagerState.currentPage.let { pagerToTab(it) }
                TabRowDefaults.PrimaryIndicator(
                    Modifier.tabIndicatorOffset(positions[idx]),
                    height = 3.dp,
                    color = selectedColor
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
        when (tab) {
            BottomTab.EMPTY -> {
                /* ----- blank slot ----- */
                Tab(
                    selected = false,
                    onClick = { /* no-op */ },
                    enabled = false,
                    content = { Box(Modifier.size(56.dp)) }   // same width as others
                )
            }
            else -> {
                val selected = pagerState.currentPage == tabToPager(index)

                Tab(
                    selected = selected,
                    onClick = { scope.launch { pagerState.scrollToPage(tabToPager(index)) } },
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
}
        /* --------- inside BottomTabRow Box scope --------- */
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.88f else 1f,
            animationSpec = spring(stiffness = 400f),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .offset(y = (-6).dp)          // slightly higher
                .size(56.dp)                   // bigger
                .scale(scale)                  // smooth press animation
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            LightOrange.copy(alpha = 0.20f),
                            colorResource(R.color.splash_background)
                        )
                    )
                )
                .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Create",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onShortsClick() }
            )
        }
        /* --------- elevated center button --------- */
//        FilledIconButton(
//            onClick = onCenterClick,
//            modifier = Modifier
//                .offset(y = (-16).dp)          // slightly higher than the bar
//                .size(56.dp)
//                .border(BorderStroke(2.dp, Color.White)),
//            shape = MaterialTheme.shapes.extraSmall,
//            colors = IconButtonDefaults.filledIconButtonColors(
//                containerColor = colorResource(R.color.splash_background),
//                contentColor = Color.White,
//
//            )
//        ) {
//            Icon(
//                imageVector = Icons.Filled.AutoAwesome,
//                contentDescription = "Center action"
//            )
//        }
    }
}
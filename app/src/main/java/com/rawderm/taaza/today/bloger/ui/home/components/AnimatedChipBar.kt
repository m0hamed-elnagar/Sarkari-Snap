package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.core.ui.theme.SandYellow

@Composable
fun AnimatedChipBar(
    labels: List<String>,
    selectedLabel: String,
    onLabelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconPainter: Painter = painterResource(R.drawable.search_list_svgrepo_com),
    chipsListState: LazyListState
) {
//    val reordered = listOf(selectedLabel) + labels.filterNot { it == selectedLabel }

    LazyRow(
        state = chipsListState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = labels,
            key = { it }                       // stable key for individual animation
        ) { label ->
            val selected = label == selectedLabel
//            AnimatedVisibility(
//                visible = true,                // we only animate placement
//                enter = slideInHorizontally() + fadeIn(),
//                exit = slideOutHorizontally() + fadeOut(),
//                modifier = Modifier.animateItem(),
//            ) {
            AssistChip(
                onClick = { onLabelSelected(label) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) SandYellow
                    else MaterialTheme.colorScheme.surface,
                    labelColor = if (selected) Color.White
                    else MaterialTheme.colorScheme.onSurface
                ),

                )
        }
    }
//    }
}
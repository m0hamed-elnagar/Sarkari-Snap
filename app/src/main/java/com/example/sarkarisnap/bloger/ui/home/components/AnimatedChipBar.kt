package com.example.sarkarisnap.bloger.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sarkarisnap.R
import com.example.sarkarisnap.core.ui.theme.SandYellow

@Composable
fun AnimatedChipBar(
    labels: List<String>,
    selectedLabel: String,
    onLabelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconPainter: Painter = painterResource(R.drawable.search_list_svgrepo_com),
    chipsListState: LazyListState
) {
    val reordered = listOf(selectedLabel) + labels.filterNot { it == selectedLabel }

    LazyRow(
        state = chipsListState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = reordered,
            key = { it }                       // stable key for individual animation
        ) { label ->
            val selected = label == selectedLabel
            AnimatedVisibility(
                visible = true,                // we only animate placement
                enter   = slideInHorizontally() + fadeIn(),
                exit    = slideOutHorizontally() + fadeOut(),
                modifier =  Modifier.animateItem(),
            ) {
                AssistChip(
                    onClick = { onLabelSelected(label) },
                    label   = { Text(label) },
                    colors  = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) SandYellow
                        else MaterialTheme.colorScheme.surface,
                        labelColor     = if (selected) Color.White
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = {
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selected) Color.White
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }
    }
}
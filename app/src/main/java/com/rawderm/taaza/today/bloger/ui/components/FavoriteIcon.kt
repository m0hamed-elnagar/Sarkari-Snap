package com.rawderm.taaza.today.bloger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.core.ui.theme.SandYellow

@Composable
fun FavoriteToggleIcon(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // pick a color that belongs to the bar’s palette
    val barColor = SandYellow      // <- bar-like
    val glowColor = barColor.copy(alpha = 0.55f)                // subtle glow

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    radius = 90f
                ),
                shape = CircleShape                               // optional circle mask
            )
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite
            else Icons.Outlined.FavoriteBorder,
            tint = Color.White,
            contentDescription = stringResource(
                if (isFavorite) R.string.remove_from_favorites
                else R.string.mark_as_favorite
            )
        )
    }

}
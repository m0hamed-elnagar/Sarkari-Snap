package com.example.sarkarisnap.bloger.ui.components

import androidx.compose.foundation.background
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
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsActions
import com.example.sarkarisnap.core.ui.theme.LightOrange

@Composable
fun FavoriteToggleIcon(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
//            .background(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        LightOrange, Color.Transparent),
//                    radius = 90f
//                )
//            )
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            tint = Color.Red,
            contentDescription = if (isFavorite) {
                stringResource(R.string.remove_from_favorites)
            } else {
                stringResource(R.string.mark_as_favorite)
            }
        )
    }

}
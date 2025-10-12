package com.rawderm.taaza.today.bloger.ui.home.components.fav

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rawderm.taaza.today.bloger.domain.Short
@Composable
fun FavoriteVideosScreen(
    shorts: List<Short>,
    onVideoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),   // 3 thumbs per row
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding        = PaddingValues(8.dp),
        modifier              = modifier.fillMaxSize()
    ) {
        items(shorts, key = { it.id }) { short ->
            ShortThumb(
                short   = short,
                onClick = {  },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
private fun ShortThumb(
    short: Short,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier) {
    AsyncImage(
        model = short.thumbUrl(),
        contentDescription = short.title,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .aspectRatio(9f / 16f)
    )
    Log.d("updatedat ", "ShortThumb: "+ short.updatedAt)
    Text(
        text = short.updatedAt,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(top = 4.dp)
    )
}
fun Short.thumbUrl(): String =
    "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
// -----------------  Thumb composable -----------------

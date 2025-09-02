package com.example.sarkarisnap.bloger.ui.postDetails.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale
import coil3.size.Size
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.components.NormalPost
import com.example.sarkarisnap.core.ui.theme.LightOrange

@Composable
 fun RelatedPostsSection(
    relatedPosts: List<Post>,
    title: String = "You may also like",
    isLoading: Boolean,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text =title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            relatedPosts.forEach { post ->
                NormalPost (
                    post = post,
                    onClick = { onPostClick(post) },
                )
            }
        }
    }

}


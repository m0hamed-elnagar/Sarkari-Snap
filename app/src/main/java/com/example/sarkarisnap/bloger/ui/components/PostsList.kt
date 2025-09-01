package com.example.sarkarisnap.bloger.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.sarkarisnap.R
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.core.ui.theme.LightOrange


@Composable
fun PostList(
    posts: List<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = scrollState) {
        itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
            Log.d("imgs", "PostList $index"+post.imageUrls)
                if (index == 0) {
                    FeaturedPost(post, onClick = { onPostClick(post) })
                } else {
                    NormalPost(post, onClick = { onPostClick(post) })
                }

        }
    }
}

@Composable
fun FeaturedPost(post: Post, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LightOrange
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            val coverImage = post.imageUrls.firstOrNull() ?: ""
            val painter = postImagePainter(coverImage)
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(
                        vertical = 6.dp,
                        horizontal = 2.dp
                    )
                    .clip(RoundedCornerShape(22.dp))


            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
            Text(
                text = post.date,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun NormalPost(post: Post, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = LightOrange
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp), // You can adjust the elevation

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp
                    ),
                    color = Color.DarkGray
                )
            }

            Spacer(Modifier.width(8.dp))
            val coverImage = post.imageUrls.firstOrNull() ?: ""
            val painter = postImagePainter(coverImage)
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun postImagePainter(imageUrl: String) = rememberAsyncImagePainter(
    model = imageUrl,
    placeholder = painterResource(R.drawable.news_placeholder),
    error = painterResource(R.drawable.news_placeholder),
    contentScale = ContentScale.Crop
)

package com.rawderm.taaza.today.bloger.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.home.PostUiItem

@Composable
fun PostListWithAds(
    modifier: Modifier = Modifier,
    pagedUiItem: LazyPagingItems<PostUiItem>? = null,
    onPostClick: (Post) -> Unit,
    scrollState: LazyListState,
) {
    val noOpConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override suspend fun onPreFling(available: Velocity) = Velocity.Zero
            override suspend fun onPostFling(consumed: Velocity, available: Velocity) =
                Velocity.Zero
        }
    }
    if (pagedUiItem != null) {
        LazyColumn(
            state = scrollState,
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(noOpConnection)
                .clipToBounds(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = pagedUiItem.itemCount,
                key = { idx ->
                    when (val item = pagedUiItem[idx]) {
                        is PostUiItem -> if (item.isAd) "ad_$idx" else item.post?.id ?: "null_$idx"
                        else -> "unknown_$idx"
                    }
                }
            ) { index ->
                val uiItem = pagedUiItem[index]

                when (uiItem) {
                    is PostUiItem -> {
                        if (uiItem.isAd) {
                            BannerAd(adUnitId = "ca-app-pub-7395572779611582/6858599226")
                        } else {
                            uiItem.post?.let { post ->
                                if (index == 0) {
                                    // ✅ Special layout for the first item
                                    FeaturedPost(
                                        post = post,
                                        onClick = { onPostClick(post) }
                                    )
                                } else {
                                    NormalPost(
                                        post = post,
                                        onClick = { onPostClick(post) }
                                    )
                                }
                            }
                        }
                    }

                    null -> {
                        // Optional: a placeholder or loading shimmer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color.Black)
                        )

                    }
                }

            }
            // Pagination loading indicator
            item {
                if (pagedUiItem.loadState.append.endOfPaginationReached.not() && pagedUiItem.itemCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

enum class CardStyle { MIXED, ALL_FEATURED, ALL_NORMAL }

@Composable
fun PostList(
    posts: LazyPagingItems<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    cardStyle: CardStyle = CardStyle.MIXED   // <-- new

) {
    val noOpConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override suspend fun onPreFling(available: Velocity) = Velocity.Zero
            override suspend fun onPostFling(consumed: Velocity, available: Velocity) =
                Velocity.Zero
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(noOpConnection)
            .clipToBounds(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = posts.itemCount, key = { idx -> posts[idx]?.id ?: "" }) { index ->
            val post = posts[index]
            post?.let {
            when (cardStyle) {               // <-- choose layout
                CardStyle.ALL_FEATURED -> FeaturedPost(post, onClick = { onPostClick(post) })
                CardStyle.ALL_NORMAL -> NormalPost(post, onClick = { onPostClick(post) })
                CardStyle.MIXED -> if (index == 0) FeaturedPost(
                    post,
                    onClick = { onPostClick(post) })
                else NormalPost(post, onClick = { onPostClick(post) })
            }
        }}
        // Pagination loading indicator
        item {
            if (posts.loadState.append.endOfPaginationReached.not() && posts.itemCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun PostListStatic(
    posts: List<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {
    val noOpConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ) = androidx.compose.ui.geometry.Offset.Zero

            override suspend fun onPreFling(available: Velocity) = Velocity.Zero
            override suspend fun onPostFling(consumed: Velocity, available: Velocity) =
                Velocity.Zero
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(noOpConnection)
            .clipToBounds(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
            if (index == 0) {
                FeaturedPost(post, onClick = { onPostClick(post) })
            } else {
                NormalPost(post, onClick = { onPostClick(post) })
            }
        }
    }
}

@Composable
fun AdItemComposable(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.1f))
    ) {

        BannerAd(
            modifier = Modifier.fillMaxWidth(),
//                nativeAdUnitID = "ca-app-pub-7395572779611582/5930969860"
        )
    }
}

@Composable
fun FeaturedPost(post: Post, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),

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

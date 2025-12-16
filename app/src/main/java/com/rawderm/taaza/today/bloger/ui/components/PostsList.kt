package com.rawderm.taaza.today.bloger.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.rememberAsyncImagePainter
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.articleDetails.AdminNotificationFeature
import com.rawderm.taaza.today.bloger.ui.components.ads.NativeScreen
import com.rawderm.taaza.today.bloger.ui.home.PostUiItem
import com.yariksoffice.lingver.Lingver

@Composable
fun PostListWithAds(
    modifier: Modifier = Modifier,
    pagedUiItem: LazyPagingItems<PostUiItem>? = null,
    onPostClick: (Post) -> Unit,
    scrollState: LazyListState,
) {
    if (pagedUiItem != null) {

        LazyColumn(
            state = scrollState,
            modifier = modifier
                .fillMaxSize()
                .clipToBounds(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = pagedUiItem.itemCount,
                key = { index -> getKeyForItem(pagedUiItem, index)
                }
            ) { index ->
                val uiItem = pagedUiItem[index]

                when (uiItem) {
                    is PostUiItem -> {
                        if (uiItem.isAd) {
                            AdItem()
                        } else {
                            uiItem.post?.let { post ->
                                FeaturedPost(
                                    post = post,
                                    onClick = { onPostClick(post) }
                                )
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
        }
    }
}
private fun getKeyForItem(pagedUiItem: LazyPagingItems<PostUiItem>, index: Int): Any {
    return when (val item = pagedUiItem[index]) {
        is PostUiItem -> if (item.isAd) "ad_$index" else item.post?.id ?: "null_$index"
        else -> "unknown_$index"
    }
}
@Composable
private fun AdItem() {

    var isAdLoaded by remember { mutableStateOf(false) }
    val onAdLoaded: (Boolean) -> Unit = { loaded ->
        isAdLoaded = loaded
    }
    val adHeight by animateDpAsState(if (isAdLoaded) 400.dp else 0.dp)


    if (BuildConfig.FLAVOR != "admin") {
    NativeScreen(
        nativeAdUnitID = "ca-app-pub-7395572779611582/2939768964",
        modifier = Modifier
            .fillMaxWidth()
            .height(adHeight),
        onAdResult = onAdLoaded
    )
}}

enum class CardStyle { MIXED, ALL_FEATURED, ALL_NORMAL }

@Composable
fun PostList(
    posts: LazyPagingItems<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    cardStyle: CardStyle = CardStyle.MIXED   // <-- new

) {


    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = posts.itemCount,
            key = { idx -> posts[idx]?.id ?: "" }) { index ->
            val post = posts[index]
            post?.let {
                when (cardStyle) {               // <-- choose layout
                    CardStyle.ALL_FEATURED -> FeaturedPost(
                        post,
                        onClick = { onPostClick(post) })

                    CardStyle.ALL_NORMAL -> NormalPost(
                        post,
                        onClick = { onPostClick(post) })

                    CardStyle.MIXED -> if (index == 0) FeaturedPost(
                        post,
                        onClick = { onPostClick(post) })
                    else NormalPost(post, onClick = { onPostClick(post) })
                }
            }
        }
        // Pagination loading indicator
        item {
            if (posts.loadState.append.endOfPaginationReached.not() && posts.itemCount > 0) {
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

@Composable
fun PostListStatic(
    posts: List<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {
    LazyColumn(
        state = scrollState,
        modifier = modifier
            .fillMaxSize()
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


@SuppressLint("LogNotTimber")
@Composable
fun FeaturedPost(post: Post, onClick: () -> Unit) {
    var showSendNotifDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

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
                        .padding(vertical = 6.dp, horizontal = 2.dp)
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
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(8.dp))
            }

            if (BuildConfig.FLAVOR == "admin") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = .20f),
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.TopEnd)

                        .clickable {
                            Log.d("TopBar", "Send Notifications clicked")
                            showSendNotifDialog = true
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sent_notifications),
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp),
                    )
                }
            }
        }

        val appUrl = stringResource(R.string.app_url)
        val postUrl = "$appUrl/" + Lingver.getInstance()
            .getLocale().language + "/post/" + post.id
        AdminNotificationFeature(
            showSendNotifDialog = showSendNotifDialog,
            onDismiss = { showSendNotifDialog = false },
            initialToken = post.labels.firstOrNull() ?: "",
            initialTitle = post.title,
            initialBody = "click to open",
            initialDeeplink = postUrl,
            context = context,
            scope = scope
        )
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

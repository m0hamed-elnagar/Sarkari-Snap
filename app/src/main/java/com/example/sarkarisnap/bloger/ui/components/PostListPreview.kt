package com.example.sarkarisnap.bloger.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sarkarisnap.bloger.domain.Post

val samplePosts = listOf(
    Post(
        "1",
        title = "Featured Post",
        description = "This is the featured post description.",
        date = "2025-08-31",
        imageUrls = listOf("https://via.placeholder.com/600x400"),
        url = ""
    ),
    Post(
        id = "2",
        title = "Second Post",
        description = "Description for the second post.",
        date = "2025-08-30",
        imageUrls = listOf("https://via.placeholder.com/600x400"),
        url = ""
    ),
    Post(
        id = "3",
        title = "Third Post",
        description = "Description for the third post.",
        date = "2025-08-29",
        imageUrls = listOf("https://via.placeholder.com/600x400"),
        url = "")
)

@Preview(showBackground = true)
@Composable
fun PostListPreview() {

    val postsListState = rememberLazyListState()

    PostList(posts = samplePosts,
        onPostClick = {},
        modifier = Modifier.fillMaxSize(),
        scrollState =postsListState
        )
}

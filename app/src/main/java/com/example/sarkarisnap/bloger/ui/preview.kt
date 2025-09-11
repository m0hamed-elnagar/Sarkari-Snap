package com.example.sarkarisnap.bloger.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsScreen
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsState

@Preview(showBackground = true)
@Composable
private fun PostDetailsScreenPreview() {
    val fakePost = Post(
        id = "1",
        title = "Sample Article Title",
        content = "<p>This is some sample HTML content for preview.</p>",
        date = "2025-09-02",
        labels = listOf("Tech", "News"),
        imageUrls = listOf(),
        url = "",
        description = "Sample description for the article.",
    )

    val fakeState = PostDetailsState(
        post = fakePost,
        isFavorite = true,
        isLoadingRelated = false,
        relatedPosts = emptyList()
    )

//    PostDetailsScreen(
//        state = fakeState,
//        onAction = {}
//    )
}

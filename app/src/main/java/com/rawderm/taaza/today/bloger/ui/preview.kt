package com.rawderm.taaza.today.bloger.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.articleDetails.PostDetailsState

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
        selfUrl = "",
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

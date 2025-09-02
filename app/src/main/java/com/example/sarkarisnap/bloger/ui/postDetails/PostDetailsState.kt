package com.example.sarkarisnap.bloger.ui.postDetails

import com.example.sarkarisnap.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class PostDetailsState (
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val post: Post? = null,
    val isLoadingRelated: Boolean = false,
    val relatedPosts: List<Post> = emptyList(),
    val relatedFetched: Boolean = false,
    val relatedError: UiText? = null,
    val isLoadingLatestArticles: Boolean = false,
    val latestArticlesPosts: List<Post> = emptyList(),
    val latestArticlesFetched: Boolean = false,
    val latestArticlesError: UiText? = null
)
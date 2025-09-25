package com.rawderm.taaza.today.bloger.ui.postDetails

import com.rawderm.taaza.today.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class PostDetailsState(
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val post: Post? = null,
    val error: UiText? = null,
    val isLoadingRelated: Boolean = false,
    val relatedPosts: List<Post> = emptyList(),
    val relatedFetched: Boolean = false,
    val relatedError: UiText? = null,
    val isLoadingLatestArticles: Boolean = false,
    val latestArticlesPosts: List<Post> = emptyList(),
    val latestArticlesFetched: Boolean = false,
    val latestArticlesError: UiText? = null
)
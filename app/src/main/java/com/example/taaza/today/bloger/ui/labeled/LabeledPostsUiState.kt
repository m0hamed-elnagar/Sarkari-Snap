package com.example.taaza.today.bloger.ui.labeled

import com.example.taaza.today.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class LabeledPostsUiState(
    val title: String = "",
    val posts: List<Post> = emptyList(),
    val favoritePosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,
)
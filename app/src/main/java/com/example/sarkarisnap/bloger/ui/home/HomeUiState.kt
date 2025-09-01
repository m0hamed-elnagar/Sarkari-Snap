package com.example.sarkarisnap.bloger.ui.home

import com.example.sarkarisnap.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class HomeUiState(
    val searchQuery: String = "",
    val posts: List<Post> = emptyList(),
    val favoritePosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,
)
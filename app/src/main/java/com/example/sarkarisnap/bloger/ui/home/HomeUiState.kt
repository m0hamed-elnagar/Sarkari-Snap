package com.example.sarkarisnap.bloger.ui.home

import com.example.sarkarisnap.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class HomeUiState(
    val labels: List<String> = emptyList(),
    val selectedLabel: String = "All",
    val favoritePosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null
)
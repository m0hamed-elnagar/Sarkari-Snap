package com.rawderm.taaza.today.bloger.ui.home

import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.Short
import com.rawderm.taaza.today.core.ui.UiText

data class HomeUiState(
    val labels: List<String> = emptyList(),
    val selectedLabel: String = "All",
    val favoritePosts: List<Post> = emptyList(),
    val favoriteShorts : List<Short> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,
    val pages: List<Page> = emptyList()
)
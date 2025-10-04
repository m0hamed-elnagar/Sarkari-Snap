package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.ui.UiText

data class ShortsState (
    val post: Post? = null,
    val favoritePosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,

    )
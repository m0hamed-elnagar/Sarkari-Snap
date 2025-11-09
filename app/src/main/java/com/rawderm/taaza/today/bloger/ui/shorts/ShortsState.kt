package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.ui.UiText

data class ShortsState(
    val favoritePosts: List<Post> = emptyList(),
    val isRefreshing: Boolean = false,
    val errorMessage: UiText? = null,
)
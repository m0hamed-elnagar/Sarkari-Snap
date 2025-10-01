package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Post

sealed interface ShortsActions {
    data object OnBackClick : ShortsActions
    data class OnPostFavoriteClick(val post: Post) : ShortsActions
    data class OnShareClick(val postId: String) : ShortsActions
    data class OnDeepLinkArrived(val postId: String) : ShortsActions
}
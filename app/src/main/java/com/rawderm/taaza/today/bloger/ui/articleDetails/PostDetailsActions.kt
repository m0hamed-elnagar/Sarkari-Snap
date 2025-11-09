package com.rawderm.taaza.today.bloger.ui.articleDetails

import com.rawderm.taaza.today.bloger.domain.Post

sealed interface PostDetailsActions {
    data object OnBackClick : PostDetailsActions
    data class OnPostFavoriteClick(val post: Post) : PostDetailsActions
    data class OnSelectedPostChange(val post: Post) : PostDetailsActions
    data class OnLinkClicked(val url: String) : PostDetailsActions
    data class OnRelatedPostClick(val post: Post) : PostDetailsActions
    data class OnLabelClick(val label: String) : PostDetailsActions
    data class OnDeepLinkArrived(val lang: String, val postId: String) : PostDetailsActions
}
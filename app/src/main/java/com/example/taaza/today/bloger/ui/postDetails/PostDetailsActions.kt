package com.example.taaza.today.bloger.ui.postDetails

import com.example.taaza.today.bloger.domain.Post

sealed interface PostDetailsActions {
    data object OnBackClick : PostDetailsActions
    data class OnPostFavoriteClick(val post: Post) : PostDetailsActions
    data class OnSelectedPostChange(val post: Post) : PostDetailsActions
    data class OnLinkClicked(val url: String) : PostDetailsActions
    data class OnRelatedPostClick(val post: Post) : PostDetailsActions
    data class OnLabelClick(val label: String) : PostDetailsActions
    data class OnDeepLinkArrived(val postId: String) : PostDetailsActions
}
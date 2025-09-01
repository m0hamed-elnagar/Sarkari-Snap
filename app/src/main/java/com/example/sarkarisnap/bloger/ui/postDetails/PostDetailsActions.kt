package com.example.sarkarisnap.bloger.ui.postDetails

import com.example.sarkarisnap.bloger.domain.Post

sealed interface PostDetailsActions {
    data object OnBackClick :PostDetailsActions
    data class OnPostFavoriteClick(val post: Post) : PostDetailsActions
    data class OnSelectedPostChange(val post: Post) : PostDetailsActions
}
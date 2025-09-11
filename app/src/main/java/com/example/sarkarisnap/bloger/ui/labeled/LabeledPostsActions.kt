package com.example.sarkarisnap.bloger.ui.labeled

import com.example.sarkarisnap.bloger.domain.Post

sealed interface LabeledPostsActions {
    data object OnBackClick :LabeledPostsActions

    data class OnPostClick(val post: Post) : LabeledPostsActions
    data class OnPostFavoriteClick(val post: Post) : LabeledPostsActions
    object OnRefresh : LabeledPostsActions
}
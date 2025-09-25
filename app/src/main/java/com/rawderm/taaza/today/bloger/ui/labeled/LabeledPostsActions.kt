package com.rawderm.taaza.today.bloger.ui.labeled

import com.rawderm.taaza.today.bloger.domain.Post

sealed interface LabeledPostsActions {
    data object OnBackClick : LabeledPostsActions

    data class OnPostClick(val post: Post) : LabeledPostsActions
    data class OnPostFavoriteClick(val post: Post) : LabeledPostsActions
    object OnRefresh : LabeledPostsActions
}
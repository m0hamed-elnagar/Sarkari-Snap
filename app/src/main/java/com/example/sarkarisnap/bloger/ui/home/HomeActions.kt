package com.example.sarkarisnap.bloger.ui.home

import com.example.sarkarisnap.bloger.domain.Post

sealed interface HomeActions {
    data class OnSearchQueryChange(val query: String) : HomeActions
    data class OnTabSelected(val index: Int) :HomeActions
    data class OnPostClick(val post: Post) : HomeActions
    data class OnPostFavoriteClick(val post: Post) : HomeActions
}
package com.example.taaza.today.bloger.ui.home

import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post

sealed interface HomeActions {
    data class OnSearchQueryChange(val query: String) : HomeActions
    data class OnTabSelected(val index: Int) : HomeActions
    data class OnPostClick(val post: Post) : HomeActions
    data class OnPageClick(val page: Page) : HomeActions
    data class OnPostFavoriteClick(val post: Post) : HomeActions
    data class OnLabelSelected(val label: String) : HomeActions
    object OnRefresh : HomeActions
    object OnNextPage : HomeActions

}
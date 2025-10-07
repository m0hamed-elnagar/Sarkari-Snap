package com.rawderm.taaza.today.bloger.ui.home

import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post

sealed interface HomeActions {
    data class OnSearchQueryChange(val query: String) : HomeActions
    data class OnTabSelected(val index: Int) : HomeActions
    data class OnPostClick(val post: Post) : HomeActions
    data class OnPageClick(val page: Page) : HomeActions
    data class OnPostFavoriteClick(val post: Post) : HomeActions
    data class OnLabelSelected(val label: String) : HomeActions
    data class ChangeLanguage(val language: String) : HomeActions
    object OnRefresh : HomeActions
    object OnNextPage : HomeActions
    object OnLoading : HomeActions
    object OnShortsClick : HomeActions


}
package com.rawderm.taaza.today.bloger.ui.home

import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post

sealed interface HomeActions {
    data class OnTabSelected(val index: Int) : HomeActions
    data class OnPostClick(val post: Post) : HomeActions
    data class OnQuickClick(val postId: String) : HomeActions
    data class OnPageClick(val page: Page) : HomeActions
    data class OnLabelSelected(val label: String) : HomeActions
    object OnRefresh : HomeActions
    object OnLoading : HomeActions


}
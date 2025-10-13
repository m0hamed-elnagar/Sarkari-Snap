package com.rawderm.taaza.today.bloger.ui.shorts

sealed interface ShortsActions {
    data object OnBackClick : ShortsActions
    data class OnPostFavoriteClick(val shortUiItem: ShortUiItem) : ShortsActions
    data class OnGetShortsByDate(val date: String) : ShortsActions
}
package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.Short

sealed interface ShortsActions {
    data object OnBackClick : ShortsActions
    data class OnPostFavoriteClick(val shortUiItem: ShortUiItem) : ShortsActions
    data class OnShareClick(val shortId: String) : ShortsActions
    data class OnDeepLinkArrived(val date: String) : ShortsActions
}
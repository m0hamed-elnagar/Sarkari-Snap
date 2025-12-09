package com.rawderm.taaza.today.bloger.ui.quiks

import com.rawderm.taaza.today.bloger.domain.Page

sealed interface QuiksActions {
    data object OnBackClick : QuiksActions
    data class OnGetShortsByDate(val date: String, val lang: String? = null) : QuiksActions
    data object OnRefresh : QuiksActions
    data class OnQuickClick(val postId: String) : QuiksActions
    data class OnPageClick(val page: Page) : QuiksActions



}
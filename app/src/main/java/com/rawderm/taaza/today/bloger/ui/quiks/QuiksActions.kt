package com.rawderm.taaza.today.bloger.ui.quiks

sealed interface QuiksActions {
    data object OnBackClick : QuiksActions
    data class OnGetShortsByDate(val date: String, val lang: String? = null) : QuiksActions
    data object OnRefresh : QuiksActions
    data class OnQuickClick (val postId: String): QuiksActions
}
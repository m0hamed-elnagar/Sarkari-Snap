package com.rawderm.taaza.today.bloger.ui.pageDetails

import com.rawderm.taaza.today.bloger.domain.Page

sealed interface PageDetailsActions {
    data object OnBackClick : PageDetailsActions
    data class OnSelectedPageChange(val page: Page) : PageDetailsActions
    data class OnLinkClicked(val url: String) : PageDetailsActions
}
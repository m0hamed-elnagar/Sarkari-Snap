package com.example.taaza.today.bloger.ui.pageDetails

import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post

sealed interface PageDetailsActions {
    data object OnBackClick : PageDetailsActions
    data class OnSelectedPageChange(val page: Page) : PageDetailsActions
    data class OnLinkClicked(val url: String) : PageDetailsActions
}
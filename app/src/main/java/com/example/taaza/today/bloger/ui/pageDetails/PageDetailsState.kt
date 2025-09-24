package com.example.taaza.today.bloger.ui.pageDetails

import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post
import com.plcoding.bookpedia.core.presentation.UiText

data class PageDetailsState(
    val isLoading: Boolean = true,
    val page: Page? = null,
    val error: UiText? = null,

    )
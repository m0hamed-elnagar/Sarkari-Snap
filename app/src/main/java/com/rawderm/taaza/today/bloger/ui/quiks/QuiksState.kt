package com.rawderm.taaza.today.bloger.ui.quiks

import com.rawderm.taaza.today.core.ui.UiText

data class QuiksState(
    val isRefreshing: Boolean = false,
    val errorMessage: UiText? = null,
)
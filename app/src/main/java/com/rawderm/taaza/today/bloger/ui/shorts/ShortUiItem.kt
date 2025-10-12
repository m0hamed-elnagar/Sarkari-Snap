package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Short

data class ShortUiItem(
    val short: Short,
    val isFavorite: Boolean = false
)
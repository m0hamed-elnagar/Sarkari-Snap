package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Short

data class ShortUiItem  (
    val short: Short?,
    val isAd: Boolean,
    val isFavorite: Boolean
) {
    companion object {
        fun post(short: Short, isFavorite: Boolean) =
            ShortUiItem(short = short, isAd = false, isFavorite = isFavorite)

        fun ad() =
            ShortUiItem(short = null, isAd = true, isFavorite = false)
    }
}

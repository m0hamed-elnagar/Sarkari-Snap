package com.rawderm.taaza.today.bloger.ui.shorts

import com.rawderm.taaza.today.bloger.domain.Short
import java.util.UUID

data class ShortUiItem(
    val short: Short?,
    val isAd: Boolean,
    val isFavorite: Boolean,
    val adId: String? = null // Unique ID for ads
) {
    companion object {
        fun post(short: Short, isFavorite: Boolean) =
            ShortUiItem(short = short, isAd = false, isFavorite = isFavorite, adId = null)

        fun ad() =
            ShortUiItem(
                short = null,
                isAd = true,
                isFavorite = false,
                adId = UUID.randomUUID().toString()
            )
    }
}
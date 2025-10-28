package com.rawderm.taaza.today.bloger.ui.quiks

import com.rawderm.taaza.today.bloger.domain.Post
import java.util.UUID

data class QuikUiItem(
    val quik: Post?,
    val isAd: Boolean,
    val adId: String? = null
) {
    companion object {
        fun post(quik: Post) =
            QuikUiItem(quik = quik, isAd = false)

        fun ad() =
            QuikUiItem(quik = null, isAd = true, adId = UUID.randomUUID().toString())
    }
}
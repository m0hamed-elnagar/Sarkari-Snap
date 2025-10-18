package com.rawderm.taaza.today.bloger.ui.home

import com.rawderm.taaza.today.bloger.domain.Post

data class PostUiItem (
    val post: Post?,
    val isAd: Boolean
) {
    companion object {
        fun post(post: Post) = PostUiItem(post = post, isAd = false)
        fun ad() = PostUiItem(post = null, isAd = true)
    }
}

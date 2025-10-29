package com.rawderm.taaza.today.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object BlogGraph : Route
    @Serializable
    data class BlogHome(val lang: String = "hi",val type :String?= null, val date: String? = null) : Route

    @Serializable
    data class PostDetails(val lang: String = "hi", val postId: String) : Route

    @Serializable
    data class PageDetails(val pageId: String) : Route
    @Serializable
    data class LabeledPosts(val label: String) : Route
    @Serializable
    data object Shorts : Route

}
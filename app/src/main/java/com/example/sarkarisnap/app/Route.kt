package com.plcoding.bookpedia.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object BlogGraph : Route
    @Serializable
    data object BlogHome : Route
    @Serializable
    data class PostDetails(val postId: String) : Route
    @Serializable
    data class LabeledPosts(val label: String) : Route

}
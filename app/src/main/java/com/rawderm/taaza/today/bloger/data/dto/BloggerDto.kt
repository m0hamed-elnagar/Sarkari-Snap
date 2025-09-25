package com.rawderm.taaza.today.bloger.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BloggerResponse(
    @SerialName("nextPageToken")
    val nextPageToken: String? = null,   // ← optional
    @SerialName("items")
    val items: List<PostDto> = emptyList()
)

@Serializable
data class PostDto(
    val id: String,
    val updated: String,
    val url: String,
    val title: String,
    val content: String,
    val labels: List<String> = emptyList(),
)

@Serializable
data class LabelsResponse(
    val items: List<LabelItem> = emptyList()
)

@Serializable
data class LabelItem(
    val labels: List<String> = emptyList()
)

@Serializable
data class PageDto(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val updated: String="",

    )

@Serializable
data class PagesResponse(
    val items: List<PageDto> = emptyList()
)

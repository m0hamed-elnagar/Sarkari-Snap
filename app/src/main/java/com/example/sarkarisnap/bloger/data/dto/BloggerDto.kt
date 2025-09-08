package com.example.sarkarisnap.bloger.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BloggerResponse(
    val items: List<PostDto> = emptyList()
)

@Serializable
data class PostDto(
    val id: String,
    val updated: String,
    val url: String,
    val title: String,
    val content: String,
    val labels : List<String> = emptyList(),
)

@kotlinx.serialization.Serializable
data class LabelsResponse(
    val items: List<LabelItem> = emptyList()
)

@kotlinx.serialization.Serializable
data class LabelItem(
    val labels: List<String> = emptyList()
)

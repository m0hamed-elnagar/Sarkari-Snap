package com.example.taaza.today.bloger.domain

data class Post(
    val id: String,
    val title: String,
    val url: String,
    val description: String,
    val content: String = "",
    val labels: List<String> = emptyList(),
    val imageUrls: List<String>,
    val date: String
)

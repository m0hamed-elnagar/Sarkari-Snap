package com.example.sarkarisnap.bloger.domain

data class Post(
    val id: String,
    val title: String,
    val url: String,
    val description: String,
    val content: String = "",
//    val imageUrl: String?,
    val imageUrls: List<String>,
    val date: String
)

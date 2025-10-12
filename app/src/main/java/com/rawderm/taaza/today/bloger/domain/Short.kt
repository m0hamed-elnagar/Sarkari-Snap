package com.rawderm.taaza.today.bloger.domain

data class Short (
    val id: String,
    val title: String,
    val selfUrl: String,
    val description: String = "",
    val videoId: String,
    val content: String = "",
    val labels: List<String> = emptyList(),
    val date: String,
    val rowDate: String = "",
    val updatedAt:String = ""
)
package com.rawderm.taaza.today.bloger.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Posts")
class PostEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val url: String,
    val description: String,
    val content: String = "",
    val labels: List<String> = emptyList(),
    val imageUrls: List<String>,
    val date: String,
    val rowDate: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "Shorts")
data class ShortEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val selfUrl: String,
    val videoId: String,
    val content: String = "",
    val date: String,
    val rowDate: String = "",
    val description: String = "",
    val labels: List<String>,
    val updatedAt: Long = System.currentTimeMillis()
)
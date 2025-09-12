package com.example.taaza.today.bloger.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePostDao {
    @Upsert
    suspend fun upsert(post: PostEntity)

    @Query("SELECT * FROM Posts ORDER BY addedAt DESC")
    fun getAllFavoriteBook(): Flow<List<PostEntity>>


    @Query("SELECT * FROM Posts WHERE id = :id")
    suspend fun getFavoriteBook(id: String): PostEntity?

    @Query("DELETE FROM Posts WHERE id = :id")
    suspend fun deleteFavoriteBook(id: String)
}
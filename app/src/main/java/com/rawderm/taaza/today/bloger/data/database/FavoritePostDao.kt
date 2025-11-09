package com.rawderm.taaza.today.bloger.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePostDao {
    @Upsert
    suspend fun upsert(post: PostEntity)

    @Query("SELECT * FROM Posts ORDER BY addedAt DESC")
    fun getAllFavoritePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM Posts WHERE id = :id")
    suspend fun getFavoritePost(id: String): PostEntity?

    @Query("DELETE FROM Posts WHERE id = :id")
    suspend fun deleteFavoritePost(id: String)
}

@Dao
interface ShortDao {
    @Query("SELECT id FROM Shorts")
    fun observeIds(): Flow<List<String>>

    @Upsert
    suspend fun upsert(short: ShortEntity)

    @Query("SELECT * FROM Shorts ORDER BY updatedAt DESC")
    fun getAllShort(): Flow<List<ShortEntity>>

    @Query("SELECT * FROM Shorts WHERE id = :id")
    suspend fun getShortById(id: String): ShortEntity?

    @Query("DELETE FROM Shorts WHERE id = :id")
    suspend fun deleteShort(id: String)
}
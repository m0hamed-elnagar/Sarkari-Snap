package com.example.sarkarisnap.bloger.domain

import com.example.sarkarisnap.bloger.data.database.PostEntity
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface PostsRepo {
    suspend fun getHomePosts(limit: Int =20): Result<List<Post>, DataError.Remote>
    suspend fun getRelatedPosts( limit: Int ,label:String): Result<List<Post>, DataError.Remote>
    suspend fun getFavoritePosts(): Flow<List<Post>>
    fun isPostFavorite(postId: String): Flow<Boolean>
    suspend fun markPostAsFavorite(post: Post): EmptyResult<DataError.Local>
    suspend fun removePostFromFavorites(postId: String)
    suspend fun getLabels(): Result<List<String>, DataError.Remote>
}
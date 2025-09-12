package com.example.taaza.today.bloger.domain

import androidx.paging.PagingData
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface PostsRepo {
    suspend fun getPosts(
        limit: Int = 20,
        label: String? = null,
        pageToken: String? = null
    ): Result<Pair<List<Post>, String?>, DataError.Remote>

    suspend fun getFavoritePosts(): Flow<List<Post>>
    fun isPostFavorite(postId: String): Flow<Boolean>
    suspend fun markPostAsFavorite(post: Post): EmptyResult<DataError.Local>
    suspend fun removePostFromFavorites(postId: String)
    suspend fun getLabels(): Result<List<String>, DataError.Remote>
    fun getPagedPosts(label: String? = null): Flow<PagingData<Post>>
}
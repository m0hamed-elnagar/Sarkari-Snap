package com.rawderm.taaza.today.bloger.domain

import androidx.paging.PagingData
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.EmptyResult
import com.rawderm.taaza.today.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface PostsRepo {
    suspend fun getFavoriteShorts(): Flow<List<Short>>
    fun isShortFavorite(shortId: String): Flow<Boolean>
    suspend fun markShortAsFavorite(short: Short): EmptyResult<DataError.Local>
    suspend fun removeShortFromFavorites(shortId: String)
    suspend fun getFavoritePosts(): Flow<List<Post>>
    fun isPostFavorite(postId: String): Flow<Boolean>
    suspend fun markPostAsFavorite(post: Post): EmptyResult<DataError.Local>
    suspend fun removePostFromFavorites(postId: String)
    suspend fun getLabels(): Result<List<String>, DataError.Remote>
    fun getPagedPosts(label: String? = null): Flow<PagingData<Post>>
     fun getPages(): Flow<PagingData<Page>>
    fun getPostsAfterDate(label: String?, afterDate: String?): Flow<PagingData<Post>>
    suspend fun getPage(pageId: String): Result<Page, DataError.Remote>
   suspend fun getPostById(postId: String) : Result<Post, DataError.Remote>
    fun getPagedShorts(): Flow<PagingData<Post>>
    fun getShortsBeforeDate(afterDate: String?): Flow<PagingData<Short>>
    // New method that accepts a language parameter
    fun getShortsBeforeDateWithLanguage(afterDate: String?, language: String): Flow<PagingData<Short>>
    fun observeFavoriteShortIds(): Flow<Set<String>>
}
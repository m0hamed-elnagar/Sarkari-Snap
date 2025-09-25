package com.example.taaza.today.bloger.data.network


import com.example.taaza.today.bloger.data.dto.BloggerResponse
import com.example.taaza.today.bloger.data.dto.LabelsResponse
import com.example.taaza.today.bloger.data.dto.PageDto
import com.example.taaza.today.bloger.data.dto.PagesResponse
import com.example.taaza.today.bloger.data.dto.PostDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemotePostDataSource {
    suspend fun getPosts(
        limit: Int,
        label: String? = null,
        pageToken: String? = null
    ): Result<BloggerResponse, DataError.Remote>

    suspend fun getUniqueLabels(limit: Int = 50): Result<LabelsResponse, DataError.Remote>

    suspend fun getPages(): Result<PagesResponse, DataError.Remote>
    suspend fun getPage(pageId: String): Result<PageDto, DataError.Remote>
    suspend fun getPost(postId: String): Result<PostDto, DataError.Remote>


    suspend fun getPostsAfterDate(
        limit: Int,
        label: String?,
        afterDate: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote>
}
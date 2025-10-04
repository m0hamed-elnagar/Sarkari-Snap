package com.rawderm.taaza.today.bloger.data.network


import com.rawderm.taaza.today.bloger.data.dto.BloggerResponse
import com.rawderm.taaza.today.bloger.data.dto.LabelsResponse
import com.rawderm.taaza.today.bloger.data.dto.PageDto
import com.rawderm.taaza.today.bloger.data.dto.PagesResponse
import com.rawderm.taaza.today.bloger.data.dto.PostDto
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.Result

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
        beforeDate: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote>



    suspend fun getShorts(limit: Int, pageToken: String?): Result<BloggerResponse, DataError.Remote>
    suspend fun getshort(postId: String): Result<PostDto, DataError.Remote>
    suspend fun getShortsBeforeDate(
        limit: Int,
        label: String? = null,
        beforeDate: String?,
        pageToken: String? = null
    ): Result<BloggerResponse, DataError.Remote>
}
package com.example.taaza.today.bloger.data.network


import com.example.taaza.today.bloger.data.dto.BloggerResponse
import com.example.taaza.today.bloger.data.dto.LabelsResponse
import com.example.taaza.today.bloger.data.dto.PageDto
import com.example.taaza.today.bloger.data.dto.PagesResponse
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


}
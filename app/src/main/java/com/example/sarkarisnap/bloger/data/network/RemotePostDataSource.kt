package com.example.sarkarisnap.bloger.data.network

import com.example.sarkarisnap.bloger.data.dto.BloggerResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemotePostDataSource {
    suspend fun getHomePosts(limit: Int): Result<BloggerResponse, DataError.Remote>
    suspend fun getRelatedPosts(limit: Int,label:String): Result<BloggerResponse, DataError.Remote>

}
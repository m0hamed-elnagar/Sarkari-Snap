package com.example.sarkarisnap.bloger.data.network

import com.example.sarkarisnap.bloger.data.dto.BloggerResponse
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface RemotePostDataSource {
    suspend fun getHomePosts(): Result<BloggerResponse, DataError.Remote>
}
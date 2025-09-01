package com.example.sarkarisnap.bloger.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface PostsRepo {
    suspend fun getHomePosts(): Result<List<Post>, DataError.Remote>
}
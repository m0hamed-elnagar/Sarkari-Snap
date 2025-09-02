package com.example.sarkarisnap.bloger.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface PostsRepo {
    suspend fun getHomePosts(limit: Int =20): Result<List<Post>, DataError.Remote>
    suspend fun getRelatedPosts( limit: Int ,label:String): Result<List<Post>, DataError.Remote>
}
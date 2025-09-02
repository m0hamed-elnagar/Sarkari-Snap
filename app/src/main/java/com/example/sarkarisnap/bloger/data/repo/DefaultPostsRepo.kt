package com.example.sarkarisnap.bloger.data.repo

import com.example.sarkarisnap.bloger.data.mappers.toDomain
import com.example.sarkarisnap.bloger.data.network.RemotePostDataSource
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map

class DefaultPostsRepo(
    private val remoteBookDataSource: RemotePostDataSource
) : PostsRepo {


    override suspend fun getHomePosts(limit: Int): Result<List<Post>, DataError.Remote> {
        return remoteBookDataSource.getHomePosts(limit)
            .map { dto ->
                dto.items.map {
                    toDomain(it)
                }
            }
    }

    override suspend fun getRelatedPosts(
        limit: Int,label:String
    ): Result<List<Post>, DataError.Remote> {
        return remoteBookDataSource.getRelatedPosts(limit, label)
            .map { dto ->
                dto.items.map {
                    toDomain(it)
                }
            }    }

}
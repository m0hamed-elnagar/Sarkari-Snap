package com.example.sarkarisnap.bloger.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sarkarisnap.bloger.data.mappers.toDomain
import com.example.sarkarisnap.bloger.data.network.RemotePostDataSource
import com.example.sarkarisnap.bloger.domain.Post
import com.plcoding.bookpedia.core.domain.Result

class PostsPagingSource(
    private val remoteDataSource: RemotePostDataSource,
    private val label: String?
) : PagingSource<String, Post>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        val pageToken = params.key
        val limit = params.loadSize
        return when (val result = remoteDataSource.getPosts(limit, label, pageToken)) {
            is Result.Success -> {
                val posts = result.data.items.map { toDomain(it) }
                val nextKey = result.data.nextPageToken
                LoadResult.Page(
                    data = posts,
                    prevKey = null,
                    nextKey = nextKey
                )
            }
            is Result.Error -> {
                LoadResult.Error(Exception(result.error.toString()))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, Post>): String? {
        // Return the closest page token or null to refresh from the start
        return null
    }
}

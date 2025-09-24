package com.example.taaza.today.bloger.data.paging

import androidx.paging.PagingSource
import com.example.taaza.today.bloger.data.mappers.toDomain
import com.example.taaza.today.bloger.data.network.RemotePostDataSource
import com.example.taaza.today.bloger.domain.Post
import com.plcoding.bookpedia.core.domain.Result
import java.time.Instant
import java.time.OffsetDateTime

fun postsAfterDatePagingSource(
    remote: RemotePostDataSource,
    label: String?,
    afterDate: String?
): ContentPagingSource<Post> = ContentPagingSource { key, loadSize ->

    when (val res = remote.getPostsAfterDate(loadSize, label, afterDate, key)) {
        is Result.Success -> {
            val items     = res.data.items
            val nextToken = res.data.nextPageToken

            PagingSource.LoadResult.Page(
                data    = items.map { toDomain(it) },
                prevKey = null,
                nextKey = nextToken?.let { tok ->
                    // pack token + floor so the data-source can unpack it later
                    "$tok|||$afterDate"
                }
            )
        }

        is Result.Error ->
            PagingSource.LoadResult.Error(Exception(res.error.toString()))
    }
}
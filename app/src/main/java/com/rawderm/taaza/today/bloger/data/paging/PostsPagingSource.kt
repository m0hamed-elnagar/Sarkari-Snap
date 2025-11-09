package com.rawderm.taaza.today.bloger.data.paging

import androidx.paging.PagingSource
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.domain.Result

fun postsPagingSource(
    remote: RemotePostDataSource,
    label: String?
): ContentPagingSource<Post> = ContentPagingSource { key, loadSize ->
    val excludedLabels = setOf("shorts", "video", "test 1", "test")

    when (val res = remote.getPosts(loadSize, label, key)) {
        is Result.Success -> PagingSource.LoadResult.Page(
            data = res.data.items
                .filter { post ->
                    post.labels.none { label -> label.lowercase() in excludedLabels }
                }
                .map { toDomain(it) },
            prevKey = null,
            nextKey = res.data.nextPageToken
        )

        is Result.Error -> PagingSource.LoadResult.Error(Exception(res.error.toString()))
    }
}
package com.rawderm.taaza.today.bloger.data.paging

import androidx.paging.PagingSource
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.domain.Result

fun ShortsPagingSource(
    remote: RemotePostDataSource,
): ContentPagingSource<Post> = ContentPagingSource { key, loadSize ->
     when (val res = remote.getShorts(loadSize, key)) {
        is Result.Success -> PagingSource.LoadResult.Page(
            data = res.data.items.map { toDomain(it) },
            prevKey = null,
            nextKey = res.data.nextPageToken
        )
        is Result.Error   -> PagingSource.LoadResult.Error(Exception(res.error.toString()))
    }
}
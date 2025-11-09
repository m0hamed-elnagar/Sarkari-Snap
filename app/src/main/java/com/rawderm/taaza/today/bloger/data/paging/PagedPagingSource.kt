package com.rawderm.taaza.today.bloger.data.paging

import androidx.paging.PagingSource
import com.rawderm.taaza.today.bloger.data.mappers.toPage
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.core.domain.Result

fun pagesPagingSource(
    remote: RemotePostDataSource
): ContentPagingSource<Page> = ContentPagingSource { key, loadSize ->
    // key = "startIndex" for pages
    val start = key?.toIntOrNull() ?: 0

    when (val res = remote.getPages()) {
        is Result.Success -> {
            val all = res.data.items.map { it.toPage() }
            val sub = all.drop(start).take(loadSize)
            PagingSource.LoadResult.Page(
                data = sub,
                prevKey = null,
                nextKey = (start + sub.size).takeIf { it < all.size }?.toString()
            )
        }

        is Result.Error -> PagingSource.LoadResult.Error(Exception(res.error.toString()))
    }
}
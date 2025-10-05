package com.rawderm.taaza.today.bloger.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class ContentPagingSource<T : Any>(
    private val loader: suspend (key: String?, loadSize: Int) -> LoadResult<String, T>
) : PagingSource<String, T>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, T> =
        loader(params.key, params.loadSize)

    override val keyReuseSupported: Boolean
        get() = true


    override fun getRefreshKey(state: PagingState<String, T>): String? {
        val anchor = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchor)
        return anchorPage?.prevKey ?: anchorPage?.nextKey
    }
}
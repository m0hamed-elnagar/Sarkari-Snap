package com.rawderm.taaza.today.bloger.data.paging

import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.domain.Result

fun postsBeforeDatePagingSource(
    remote: RemotePostDataSource,
    label: String?,
    endDate: String?
): ContentPagingSource<Post> = beforeDatePagingSource(
    initialEndDate = endDate,
    fetch = { loadSize, end ->
        when (val res = remote.getPostsAfterDate(loadSize, label, end, null)) {
            is Result.Success -> Result.Success(res.data.items)
            is Result.Error -> Result.Error(res.error)
        }
    },
    getUpdated = { it.updated },
    mapToDomain = { toDomain(it) }
)
package com.rawderm.taaza.today.bloger.data.paging

import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.domain.Result

fun QuiksBeforeDatePagingSource(
    remote: RemotePostDataSource,
    endDate: String?,
    language: String
): ContentPagingSource<Post> = beforeDatePagingSource(
    initialEndDate = endDate,
    fetch = { loadSize, end ->
        when (val res =
            remote.getPostsBeforeDateWithLanguage(loadSize, "Quiks", end, null, language)) {
            is Result.Success -> Result.Success(
                res.data.items
            )

            is Result.Error -> Result.Error(res.error)
        }
    },
    getUpdated = { it.updated },
    mapToDomain = { toDomain(it) }
)
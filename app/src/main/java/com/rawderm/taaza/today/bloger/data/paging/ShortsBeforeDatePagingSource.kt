package com.rawderm.taaza.today.bloger.data.paging
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.mappers.toShort
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.Short
import com.rawderm.taaza.today.core.domain.Result

fun shortsBeforeDatePagingSource(
    remote: RemotePostDataSource,
    endDate: String?
): ContentPagingSource<Short> = beforeDatePagingSource(
    initialEndDate = endDate,
    fetch = { loadSize, end ->
        when (val res = remote.getShortsBeforeDate(loadSize, "", end, null)) {
            is Result.Success -> Result.Success(res.data.items)
            is Result.Error -> Result.Error(res.error)
        }
    },
    getUpdated = { it.updated },
    mapToDomain = { it.toShort() }
)

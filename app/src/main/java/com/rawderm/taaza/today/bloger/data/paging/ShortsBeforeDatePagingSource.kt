package com.rawderm.taaza.today.bloger.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.domain.Result
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun shortsBeforeDatePagingSource(
    remote: RemotePostDataSource,
    endDate: String?
): ContentPagingSource<Post> = beforeDatePagingSource(
    initialEndDate = endDate,
    fetch = { loadSize, end ->
        when (val res = remote.getShortsBeforeDate(loadSize, "", end, null)) {
            is Result.Success -> Result.Success(res.data.items)
            is Result.Error -> Result.Error(res.error)
        }
    },
    getUpdated = { it.updated },
    mapToDomain = { toDomain(it) }
)

package com.rawderm.taaza.today.bloger.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.domain.Post
import com.plcoding.bookpedia.core.domain.Result
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun shortsBeforeDatePagingSource(
    remote: RemotePostDataSource,
    endDate: String?
): ContentPagingSource<Post> = ContentPagingSource { key, loadSize ->

    val currentEndDate = key ?: endDate

    try {
        when (val res = remote.getShortsBeforeDate(loadSize, "",currentEndDate, null)) {
            is Result.Success -> {
                val items = res.data.items

                if (items.isEmpty()) {
                    PagingSource.LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    // Get the last item's date
                    val lastItemDate = items.last().updated

                    // Subtract 1 second to make it exclusive
                    val nextEndDate = subtractOneSecond(lastItemDate)

                    Log.d("PagingSource", """
                        Loaded ${items.size} items. 
                        Current: $currentEndDate 
                        Last item date: $lastItemDate
                        Next (minus 1s): $nextEndDate
                    """.trimIndent())

                    PagingSource.LoadResult.Page(
                        data = items.map { toDomain(it) },
                        prevKey = null,
                        nextKey = nextEndDate
                    )
                }
            }

            is Result.Error -> {
                PagingSource.LoadResult.Error(Exception(res.error.toString()))
            }
        }
    } catch (e: Exception) {
        PagingSource.LoadResult.Error(e)
    }
}

private fun subtractOneSecond(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(timestamp, formatter)
        val oneSecondEarlier = dateTime.minusSeconds(1)
        oneSecondEarlier.format(formatter)
    } catch (e: Exception) {
        // If parsing fails, return the original timestamp
        Log.e("PagingSource", "Failed to parse timestamp: $timestamp", e)
        timestamp
    }
}
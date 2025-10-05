package com.rawderm.taaza.today.bloger.data.paging


import android.util.Log
import androidx.paging.PagingSource
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.Result
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Generic builder for "before/after date" pagination where the next key is derived
 * from the last item's updated timestamp by subtracting one second.
 */
internal fun <T : Any, D> beforeDatePagingSource(
    initialEndDate: String?,
    fetch: suspend (loadSize: Int, endDate: String?) -> Result<List<D>, DataError.Remote>,
    getUpdated: (D) -> String,
    mapToDomain: (D) -> T
): ContentPagingSource<T> = ContentPagingSource { key, loadSize ->

    val currentEndDate = key ?: initialEndDate

    when (val res = fetch(loadSize, currentEndDate)) {
        is Result.Success -> {
            val items = res.data

            if (items.isEmpty()) {
                PagingSource.LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                val lastItemDate = getUpdated(items.last())
                val nextEndDate = subtractOneSecond(lastItemDate)

                PagingSource.LoadResult.Page(
                    data = items.map(mapToDomain),
                    prevKey = null,
                    nextKey = nextEndDate
                )
            }
        }

        is Result.Error -> PagingSource.LoadResult.Error(Exception(res.error.toString()))
    }
}

 fun subtractOneSecond(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(timestamp, formatter)
        dateTime.minusSeconds(1).format(formatter)
    } catch (e: Exception) {
        Log.e("PagingSource", "Failed to parse timestamp: $timestamp", e)
        timestamp
    }
}

 fun addOneSecond(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(timestamp, formatter)
         dateTime.plusSeconds(1).format(formatter)
    } catch (e: Exception) {
        // If parsing fails, return the original timestamp
        Log.e("PagingSource", "Failed to parse timestamp: $timestamp", e)
        timestamp
    }
}



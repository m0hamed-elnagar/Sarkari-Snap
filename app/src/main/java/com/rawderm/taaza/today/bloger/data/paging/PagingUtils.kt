package com.rawderm.taaza.today.bloger.data.paging


import androidx.paging.PagingSource
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.Result
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Builds a paging source for feeds that page by date where each subsequent page
 * is fetched with an ISO-8601 endDate strictly before the last item's updated timestamp.
 *
 * - initialEndDate: optional starting endDate (null = no constraint for first page)
 * - fetch: suspend function to fetch items for a given loadSize and endDate
 * - getUpdated: extracts the ISO-8601 updated timestamp from the DTO
 * - mapToDomain: maps DTO to domain model
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
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val dateTime = OffsetDateTime.parse(timestamp, formatter)
    return dateTime.minusSeconds(1).format(formatter)

}

fun addOneSecond(timestamp: String): String {
    if (timestamp.isBlank()) return timestamp

    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val dateTime = OffsetDateTime.parse(timestamp, formatter)
    return dateTime.plusSeconds(1).format(formatter)

}



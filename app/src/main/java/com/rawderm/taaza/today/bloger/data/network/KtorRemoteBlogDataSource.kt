package com.rawderm.taaza.today.bloger.data.network

import android.annotation.SuppressLint
import android.util.Log
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.bloger.data.dto.BloggerResponse
import com.rawderm.taaza.today.bloger.data.dto.LabelsResponse
import com.rawderm.taaza.today.bloger.data.dto.PageDto
import com.rawderm.taaza.today.bloger.data.dto.PagesResponse
import com.rawderm.taaza.today.bloger.data.dto.PostDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.OffsetDateTime

private const val BASE_URL =
    "https://www.googleapis.com/blogger/v3/blogs/190535731829050983"
private const val BASE_URL3 =
    "https://www.googleapis.com/blogger/v3/blogs/3213900"
private const val BASE_URL2 =
    "https://www.googleapis.com/blogger/v3/blogs/2399953"
private const val BASE_URL_english =
    "https://www.googleapis.com/blogger/v3/blogs/2640395952322331775"

class KtorRemoteBlogDataSource(private val httpClient: HttpClient) : RemotePostDataSource {

    private val apiKey = BuildConfig.BLOGGER_API_KEY

    @SuppressLint("LogNotTimber")
    override suspend fun getPostsAfterDate(
        limit: Int,
        label: String?,
        afterDate: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {
        Log.d(
            "KtorRemoteBlogDataSource",
            "getPostsAfterDate: label=$label, =$afterDate, limit=$limit"
        )
        val (realToken, packedDate) = pageToken
            ?.takeIf { it.contains("|||") }
            ?.split("|||")
            ?.let { it[0] to it[1] }
            ?: (null to null)

// Use a safe fallback date if both packedDate and afterDate are null/empty
        val safeAfterDate = packedDate.takeIf { !it.isNullOrEmpty() }
            ?: afterDate.takeIf { !it.isNullOrEmpty() }
            ?: "1970-01-01T00:00:00Z" // fallback default date

        val inclusive = try {
            OffsetDateTime.parse(safeAfterDate)
        } catch (e: Exception) {
            Log.w("KtorRemoteBlogDataSource", "Invalid date format: $safeAfterDate, using epoch")
            OffsetDateTime.parse("1970-01-01T00:00:00Z")
        }

        // first page
        Log.d(
            "KtorRemoteBlogDataSource",
            "getPostsAfterDate: label=$label, afterDate=$afterDate, realToken=$realToken"
        )
        return safeCall<BloggerResponse> {
            httpClient.get("$BASE_URL/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                if (!label.isNullOrEmpty() && label != "All") {
                    parameter("labels", label)
                }
                parameter("endDate", inclusive)

                realToken?.let { parameter("pageToken", it) }

                parameter("orderBy", "updated")
                parameter("fields", "nextPageToken,items(id,updated,url,title,content,labels)")
            }.body()
        }
    }

    override suspend fun getPosts(
        limit: Int,
        label: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {
        val labelLog = if (!label.isNullOrEmpty() && label != "All") label else "<none>"
        val tokenLog = pageToken ?: "<none>"
        Log.d(
            "KtorRemoteBlogDataSource",
            "getPosts: label=$labelLog, pageToken=$tokenLog, limit=$limit"
        )
        return safeCall<BloggerResponse> {
            httpClient.get("$BASE_URL/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                if (!label.isNullOrEmpty() && label != "All") {
                    parameter("labels", label)
                }
                if (pageToken != null) {
                    parameter("pageToken", pageToken)
                }
                parameter("orderBy", "updated") // Order by published date
                parameter("fields", "nextPageToken,items(id,updated,url,title,content,labels)")
            }.body()
        }
    }

    override suspend fun getUniqueLabels(limit: Int): Result<LabelsResponse, DataError.Remote> {
        return safeCall<LabelsResponse> {
            httpClient.get("$BASE_URL/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                parameter("fields", "nextPageToken,items(labels)")
            }.body()
        }
    }

    override suspend fun getPages(): Result<PagesResponse, DataError.Remote> {
        return safeCall<PagesResponse> {
            httpClient.get("$BASE_URL/pages") {
                parameter("key", apiKey)
                parameter("fields", "items(id,title,content,url,updated)")
            }.body()
        }
    }

    override suspend fun getPage(pageId: String): Result<PageDto, DataError.Remote> =
        safeCall<PageDto> {
            httpClient.get("$BASE_URL/pages/$pageId") {
                parameter("key", apiKey)
                parameter("fields", "id,title,content,url")
            }.body()
        }

    override suspend fun getPost(postId: String): Result<PostDto, DataError.Remote> =
        safeCall<PostDto> {
            httpClient.get("$BASE_URL/posts/$postId") {
                parameter("key", apiKey)
                parameter("fields", "id,updated,url,title,content,labels")
            }.body()
        }

    override suspend fun getShorts(
        limit: Int,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {
        val tokenLog = pageToken ?: "<none>"
        Log.d(
            "KtorRemoteBlogDataSource",
            "getPosts:, pageToken=$tokenLog, limit=$limit"
        )
        return safeCall<BloggerResponse> {
            httpClient.get("$BASE_URL_english/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                parameter("labels", "Video")
                if (pageToken != null) {
                    parameter("pageToken", pageToken)
                }
                parameter("orderBy", "updated") // Order by published date
                parameter("fields", "nextPageToken,items(id,updated,url,title,content,labels)")
            }.body()
        }
    }

}
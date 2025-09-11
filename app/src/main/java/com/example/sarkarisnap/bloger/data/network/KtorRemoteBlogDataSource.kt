package com.example.sarkarisnap.bloger.data.network

import android.util.Log
import com.example.sarkarisnap.BuildConfig
import com.example.sarkarisnap.bloger.data.dto.BloggerResponse
import com.example.sarkarisnap.bloger.data.dto.LabelsResponse
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL4 =
    "https://www.googleapis.com/blogger/v3/blogs/190535731829050983"
private const val BASE_URL =
    "https://www.googleapis.com/blogger/v3/blogs/3213900"
private const val BASE_URL2 =
    "https://www.googleapis.com/blogger/v3/blogs/2399953"

class KtorRemoteBlogDataSource(private val httpClient: HttpClient) : RemotePostDataSource {

    private val apiKey = BuildConfig.BLOGGER_API_KEY

    override suspend fun getPosts(
        limit: Int,
        label: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {
        val labelLog = if (!label.isNullOrEmpty() && label != "All") label else "<none>"
        val tokenLog = pageToken ?: "<none>"
        Log.d("KtorRemoteBlogDataSource", "getPosts: label=$labelLog, pageToken=$tokenLog, limit=$limit")
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
}
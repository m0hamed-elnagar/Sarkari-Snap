package com.rawderm.taaza.today.bloger.data.network

import android.util.Log
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.dto.BloggerResponse
import com.rawderm.taaza.today.bloger.data.dto.LabelsResponse
import com.rawderm.taaza.today.bloger.data.dto.PageDto
import com.rawderm.taaza.today.bloger.data.dto.PagesResponse
import com.rawderm.taaza.today.bloger.data.dto.PostDto
import com.rawderm.taaza.today.core.data.safeCall
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL =
    "https://www.googleapis.com/blogger/v3/blogs/190535731829050983"
private const val BASE_URL3 =
    "https://www.googleapis.com/blogger/v3/blogs/3213900"
private const val BASE_URL2 =
    "https://www.googleapis.com/blogger/v3/blogs/2399953"
private const val BASE_URL_english =
    "https://www.googleapis.com/blogger/v3/blogs/2640395952322331775"

class KtorRemoteBlogDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val languageDataStore: LanguageDataStore
) : RemotePostDataSource {

    private val apiKey = BuildConfig.BLOGGER_API_KEY
    private suspend fun getBaseUrl(): String {
        val lang = languageDataStore.getLanguageSync() // e.g. "hi" or "en"
        val url = when (lang) {
            "en" -> BASE_URL_english
            "hi" -> BASE_URL
            else -> BASE_URL // fallback
        }
        Log.d("lang", "getBaseUrl: $lang ")
        return url
    }
    override suspend fun getPostsAfterDate(
        limit: Int,
        label: String? ,
        beforeDate: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {


        return safeCall<BloggerResponse> {
            httpClient.get("${getBaseUrl()}/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                if (!label.isNullOrEmpty() && label != "All") {
                    parameter("labels", label)
                }

                // all posts updated before this post
                parameter("endDate", beforeDate)
                pageToken?.let { parameter("pageToken", it) }
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
            httpClient.get("${getBaseUrl()}/posts") {
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

    override suspend fun getUniqueLabels(limit: Int, currentLang: String): Result<LabelsResponse, DataError.Remote> {
        return safeCall<LabelsResponse> {
            val isHindi = currentLang.equals("hi", ignoreCase = true)


            val baseUrl = if (isHindi){BASE_URL}else BASE_URL_english
            httpClient.get("${baseUrl}/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                parameter("fields", "nextPageToken,items(labels)")
            }.body()
        }
    }

    override suspend fun getPages(): Result<PagesResponse, DataError.Remote> {
        return safeCall<PagesResponse> {
            httpClient.get("${getBaseUrl()}/pages") {
                parameter("key", apiKey)
                parameter("fields", "items(id,title,content,url,updated)")
            }.body()
        }
    }

    override suspend fun getPage(pageId: String): Result<PageDto, DataError.Remote> =
        safeCall<PageDto> {
            httpClient.get("${getBaseUrl()}/pages/$pageId") {
                parameter("key", apiKey)
                parameter("fields", "id,title,content,url")
            }.body()
        }

    override suspend fun getPost(postId: String): Result<PostDto, DataError.Remote> =
        safeCall<PostDto> {
            httpClient.get("${getBaseUrl()}/posts/$postId") {
                parameter("key", apiKey)
                parameter("fields", "id,updated,url,title,content,labels")
            }.body()
        }
  override  suspend fun getShortsBeforeDate(
        limit: Int,
        label: String? ,
        beforeDate: String?,
        pageToken: String?
    ): Result<BloggerResponse, DataError.Remote> {


        return safeCall<BloggerResponse> {
            httpClient.get("${getBaseUrl()}/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                parameter("labels", "Video")

                // all posts updated before this post
                parameter("endDate", beforeDate)
                pageToken?.let { parameter("pageToken", it) }
                parameter("orderBy", "updated")
                parameter("fields", "nextPageToken,items(id,updated,url,title,content,labels)")
            }.body()
        }
    }
    
    override suspend fun getPostsBeforeDateWithLanguage(
        limit: Int,
        label: String?,
        beforeDate: String?,
        pageToken: String?,
        language: String
    ): Result<BloggerResponse, DataError.Remote> {
        // Determine the base URL based on the language parameter
        val baseUrl = when (language) {
            "en" -> BASE_URL_english
            "hi" -> BASE_URL
            else -> BASE_URL // fallback to default
        }

        return safeCall<BloggerResponse> {
            httpClient.get("$baseUrl/posts") {
                parameter("key", apiKey)
                parameter("maxResults", limit)
                parameter("labels", label)

                // all posts updated before this post
                parameter("endDate", beforeDate)
                pageToken?.let { parameter("pageToken", it) }
                parameter("orderBy", "updated")
                parameter("fields", "nextPageToken,items(id,updated,url,title,content,labels)")
            }.body()
        }
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
            httpClient.get("${getBaseUrl()}/posts") {
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
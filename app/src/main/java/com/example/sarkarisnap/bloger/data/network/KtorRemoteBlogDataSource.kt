package com.example.sarkarisnap.bloger.data.network

import com.example.sarkarisnap.BuildConfig
import com.example.sarkarisnap.bloger.data.dto.BloggerResponse
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Error
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BASE_URL =
    "https://www.googleapis.com/blogger/v3/blogs/190535731829050983"
private const val BASE_URL2 =
    "https://www.googleapis.com/blogger/v3/blogs/3213900"
private const val BASE_URL3 =
    "https://www.googleapis.com/blogger/v3/blogs/2399953"

class KtorRemoteBlogDataSource(private val httpClient: HttpClient) : RemotePostDataSource {


    private val apiKey = BuildConfig.BLOGGER_API_KEY

    override suspend fun getHomePosts(limit: Int ): Result<BloggerResponse, DataError.Remote> {
        return safeCall<BloggerResponse> {

            httpClient
                .get("$BASE_URL/posts")
                {
                    parameter("key", apiKey)
                    parameter("maxResults", limit)
                    parameter("fields", "items(id,updated,url,title,content,labels)")
                }
                .body()
        }
    }

    override suspend fun getRelatedPosts(limit: Int,label:String): Result<BloggerResponse, DataError.Remote> {
        return safeCall<BloggerResponse> {

            httpClient
                .get("$BASE_URL/posts")
                {
                    parameter("key", apiKey)
                    parameter("maxResults", limit)
                    parameter("labels", label)
                    parameter("fields", "items(id,updated,url,title,content,labels)")
                }
                .body()
        }    }
}
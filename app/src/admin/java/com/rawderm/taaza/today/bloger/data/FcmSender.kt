package com.rawderm.taaza.today.bloger.data

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object FcmSender2 {


    private const val TAG = "FcmSender"
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/taaza-today/messages:send"
    private const val SCOPE = "https://www.googleapis.com/auth/firebase.messaging"

    private val json = Json { ignoreUnknownKeys = true }
    private val client: HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }) }
        install(Logging) { level = LogLevel.INFO }
        expectSuccess = true
    }

    /* ---------- public API ---------- */
    suspend fun sendNotification(
        context: Context,
        targetToken: String,
        title: String,
        body: String,
        deeplink: String
    ): Result = withContext(Dispatchers.IO) {

        runCatching {
            val payload = buildPayload(targetToken, title, body, deeplink)
            postToFcm( payload)
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Failure(it.message ?: "Unknown error") }
        )
    }

    /* ---------- internals ---------- */


    private fun buildPayload(
        token: String,
        title: String,
        body: String,
        deeplink: String
    ): SendMessageDto {
        val dto = SendMessageDto(
            to = token,
            notification = NotificationBody(title, body, deeplink)
        )

        return dto
    }

    private suspend fun postToFcm( dto: SendMessageDto): String =
        withContext(Dispatchers.IO) {
//             val baseUrl = "http://10.0.2.2:8080"
//            val baseUrl = "http://192.168.1.10:8080"
            val baseUrl = "https://taaza-today-fcm.vercel.app/api"

            client.post("${baseUrl}/send") {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }.body<String>()


        }

    /* ---------- result ---------- */
    sealed interface Result {
        data class Success(val raw: String) : Result
        data class Failure(val msg: String) : Result
    }
    /* ---------- DTOs ---------- */
    @Serializable
    private data class FcmDto(
        val message: Message
    ) {
        @Serializable
        data class Message(
            val token: String,
            val notification: Notification,
            val data: Map<String, String>
        )

        @Serializable
        data class Notification(
            val title: String,
            val body: String
        )
    }
}

//class FcmSender(private val context: Context) {
//val serviceAccountStream = context.assets.open("fcm_sak.json")
//    val options = FirebaseOptions.Builder()
//        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
//        .setProjectId("YOUR_PROJECT_ID")
//        .build()
//    private val gson = Gson()
//    private val okHttp = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
//        .build()
//
//    private val fcmUrl = "https://fcm.googleapis.com/v1/projects/YOUR_PROJECT_ID/messages:send"
//
//    suspend fun send(token: String, title: String, body: String, data: Map<String, String>? = null) {
//        val bearer = FcmAuth.obtainToken(context)
//        val payload = FcmMessage(
//            message = FcmMessage.Message(
//                token = token,
//                notification = FcmMessage.Notification(title, body),
//                data = data
//            )
//        )
//
//        val request = Request.Builder()
//            .url(fcmUrl)
//            .post(
//                gson.toJson(payload)
//                    .toRequestBody("application/json; charset=utf-8".toMediaType())
//            )
//            .addHeader("Authorization", "Bearer $bearer")
//            .build()
//
//        withContext(Dispatchers.IO) {
//            okHttp.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) {
//                    throw IOException("FCM error ${response.code} : ${response.body?.string()}")
//                }
//                Log.d("FCM", "Message sent → ${response.body?.string()}")
//            }
//        }
//    }
//}
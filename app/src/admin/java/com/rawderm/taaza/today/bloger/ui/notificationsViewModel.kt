//package com.rawderm.taaza.today.bloger.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.rawderm.taaza.today.bloger.data.NotificationBody
//import com.rawderm.taaza.today.bloger.data.SendMessageDto
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.engine.okhttp.OkHttp
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.plugins.logging.LogLevel
//import io.ktor.client.plugins.logging.Logging
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.coroutines.launch
//import kotlinx.serialization.json.Json
//
//class NotificationsViewModel: ViewModel() {
//    private val client: HttpClient = HttpClient(OkHttp) {
//        install(ContentNegotiation) { json(Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        }) }
//        install(Logging) { level = LogLevel.INFO }
//        expectSuccess = true
//    }
//
//
//
//
//    private val baseUrl = "http://10.0.2.2:8080"
//
//    fun sendMessage(isBroadcast: Boolean) {
//        viewModelScope.launch {
//            val messageDto = SendMessageDto(
//                to = state.remoteToken,
//                notification = NotificationBody(
//                    title = "New message!",
//                    body = state.messageText
//                )
//            )
//
//            try {
//                client.post("${baseUrl}broadcast") {
//                    contentType(ContentType.Application.Json)
//                    setBody(body)
//                }.body<String>()
//
//
//
//                state = state.copy(messageText = "")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
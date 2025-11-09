package com.rawderm.taaza.today.bloger.data

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageDto(
    val to: String?,
    val notification: NotificationBody
)

@Serializable
data class NotificationBody(
    val title: String,
    val body: String,
    val deeplink: String

)


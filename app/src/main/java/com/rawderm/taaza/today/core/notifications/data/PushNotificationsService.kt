package com.rawderm.taaza.today.core.notifications.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.app.MainActivity

class PushNotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("PushService", "New FCM token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("PushService", "Message received from: ${remoteMessage.from}")

        remoteMessage.data.let { data ->
            Log.d("PushService", "Message data payload: $data")

            val title = data["title"] ?: "Notification"
            val body = data["body"] ?: ""
            val deeplink = data["deeplink"]

            Log.d("PushService", "Parsed title: $title")
            Log.d("PushService", "Parsed body: $body")
            Log.d("PushService", "Parsed deeplink: $deeplink")

            // Channel ID – change once if you already created a silent channel
            val channelId = "general_notifications3"
            val channelName = "General Notifications"

            // Sound URI (lowercase name, no extension)
            val soundUri =
                Uri.parse("android.resource://${packageName}/raw/notifications_tone")

            // Create channel **with sound** (Android O+)
            createNotificationChannel(channelId, channelName, soundUri)

            // Deep-link intent
            val intent = if (!deeplink.isNullOrEmpty()) {
                Log.d("PushService", "Creating Intent for deeplink: $deeplink")
                Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            } else {
                Log.d("PushService", "No deeplink found. Opening MainActivity instead.")
                Intent(this, MainActivity::class.java)
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build notification – **do NOT call setSound()**
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(System.currentTimeMillis().toInt(), notification)
            Log.d("PushService", "Notification displayed successfully.")
        }
    }

    /* -------------------------------------------------------- */
    /* Channel created ONCE with custom sound + audio attributes */
    /* -------------------------------------------------------- */
    private fun createNotificationChannel(
        id: String,
        name: String,
        soundUri: Uri
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    setSound(soundUri, audioAttrs)   // sound attached to channel
                }

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

/* -------------------------------------------------------------------------- */
/* Helper for local tests – same channel rules apply                          */
/* -------------------------------------------------------------------------- */
fun showTestNotification(context: Context, title: String, body: String, deeplink: String?) {
    val channelId = "test_notifications"
    val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notifications_tone")

    // Create channel with sound (once)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val channel = NotificationChannel(channelId, "Test Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            .apply {
                setSound(soundUri, audioAttrs)
            }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    val intent = if (!deeplink.isNullOrEmpty()) {
        Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    } else {
        Intent(context, MainActivity::class.java)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.app_icon)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build() // no setSound() – channel owns the sound

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(System.currentTimeMillis().toInt(), notification)
}

package com.rawderm.taaza.today.bloger.ui.articleDetails

import android.util.Log
import androidx.compose.runtime.Composable
import com.rawderm.taaza.today.bloger.data.FcmSender2
import com.rawderm.taaza.today.bloger.ui.NotificationInputDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AdminNotificationFeature(
showSendNotifDialog: Boolean,
onDismiss: () -> Unit,
initialToken: String,
initialTitle :String,
initialBody: String,
initialDeeplink: String,
context: android.content.Context,
scope: CoroutineScope
) {
    if (showSendNotifDialog) {
        NotificationInputDialog(
            onDismiss = onDismiss,
            initialToken = initialToken,
            initialTitle = initialTitle,
            initialBody = initialBody,
            initialDeeplink = initialDeeplink,
            onSend = { token, title, body, deeplink ->
                onDismiss()
                scope.launch {
                    when (val r = FcmSender2.sendNotification(
                        context = context,
                        targetToken = token,
                        title = title,
                        body = body,
                        deeplink = deeplink
                    )) {
                        is FcmSender2.Result.Success ->
                            Log.d("PostCard", "✅ Notification sent successfully")
                        is FcmSender2.Result.Failure ->
                            Log.e("PostCard", "❌ Error sending notification: ${r.msg} $token")
                    }
                }
            }
        )
    }
}

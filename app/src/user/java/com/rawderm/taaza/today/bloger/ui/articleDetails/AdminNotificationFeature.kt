package com.rawderm.taaza.today.bloger.ui.articleDetails

import androidx.compose.runtime.Composable

@Composable
fun AdminNotificationFeature(
    showSendNotifDialog: Boolean,
    onDismiss: () -> Unit,
    initialToken: String,
    initialTitle :String,
    initialBody: String,
    initialDeeplink: String,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope
) {
    // do nothing in normal flavor
}

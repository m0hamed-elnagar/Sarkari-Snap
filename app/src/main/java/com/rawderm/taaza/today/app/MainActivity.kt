package com.rawderm.taaza.today.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import android.content.Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val postId = mutableStateOf<String?>(null)
        val uri: Uri? = intent?.data
        if (uri != null && uri.pathSegments.size >= 2) {
            postId.value = uri.pathSegments[1] // e.g. "123" from /post/123
            // Pass this ID down to Compose navigation
        }
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            App(navController, startPostId = postId.value)


        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }
}

@Composable
fun DebugComposable(name: String, content: @Composable () -> Unit) {
    val count = remember { mutableStateOf(0) }
    SideEffect {
        count.value++
        println("Recomposition #${count.value} for $name")
    }
    content()
}

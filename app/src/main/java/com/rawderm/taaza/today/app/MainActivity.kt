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
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.core.ui.theme.Transparent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Black.toArgb(),
                darkScrim = Color.Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Black.toArgb(),
                darkScrim = Color.Black.toArgb()
            )
        )

        setContent {


            val navController = rememberNavController()
            App(navController)


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

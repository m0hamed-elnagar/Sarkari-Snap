package com.rawderm.taaza.today.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rawderm.taaza.today.bloger.ui.quiks.QuikScreenRoot
import com.rawderm.taaza.today.bloger.ui.quiks.QuiksViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Black.toArgb())
        )
        setContent {

            val quikViewModel = koinViewModel<QuiksViewModel>()


            QuikScreenRoot(
                viewModel = quikViewModel,
                onBackClicked = {
                },
                onQuickClick = {
                }         )

        }

    }
    override fun onDestroy() {
        super.onDestroy()
    }
}
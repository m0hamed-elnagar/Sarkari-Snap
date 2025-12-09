package com.rawderm.taaza.today.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsActions
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
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
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "quik") {
                composable("quik") {
                    QuikScreenRoot(
                        viewModel = quikViewModel,
                        onBackClicked = { finish() },
                        onQuickClick = { /* nothing */ },
                        onPageMenuClick = { pageId ->
                            navController.navigate(Route.PageDetails(pageId)) {
                                launchSingleTop = true
                            }                        }
                    )
                }
                composable<Route.PageDetails> { backEntry ->
                    val detailsVM: PageDetailsViewModel = koinViewModel()
                    PageDetailsScreenRoot(
                        viewModel = detailsVM,
                        onBackClicked = { navController.popBackStack() }
                    )

                }
            }
        }
    }
}
package com.example.taaza.today.app

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.taaza.today.app.AppChecker.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.taaza.today.app.BloggerApplication.Companion.isWorking
import com.example.taaza.today.app.components.LoadingScreen
import com.example.taaza.today.app.components.NoInternetScreen
import com.example.taaza.today.app.components.TemporarilyStoppedScreen
import com.example.taaza.today.bloger.ui.SelectedPostViewModel
import com.example.taaza.today.bloger.ui.home.HomeScreenRoot
import com.example.taaza.today.bloger.ui.home.HomeViewModel
import com.example.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.example.taaza.today.bloger.ui.labeled.LabeledScreenRoot
import com.example.taaza.today.bloger.ui.pageDetails.PageDetailsActions
import com.example.taaza.today.bloger.ui.pageDetails.PageDetailsScreenRoot
import com.example.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsActions
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsScreenRoot
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsViewModel
import com.example.taaza.today.core.utils.checkInternet
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val context = LocalContext.current

        /* --------- single state-flow for whole check --------- */
        val checker = remember { AppChecker(context) }
        val uiState by checker.uiState.collectAsState()
        LaunchedEffect(Unit) { checker.check() }

        /* --------- retry from any error screen --------- */
        val scope = rememberCoroutineScope()
        val onRetry: () -> Unit = { scope.launch { checker.check() } }

        when (uiState) {
            UiState.Checking->AppNavigation(navController)
            UiState.Loading -> LoadingScreen()
            UiState.NoInternet -> NoInternetScreen(onRetry = onRetry)
            UiState.Stopped -> TemporarilyStoppedScreen(onRetry = onRetry)
            UiState.GaveUp -> NoInternetScreen(                           // same UI, different text
                msg = "Still can’t connect after several tries.",
                onRetry = onRetry
            )

            UiState.Working -> AppNavigation(navController)
        }
    }
}

@Composable
private fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.BlogGraph,
        enterTransition = { fadeIn(tween(800)) },
        exitTransition = { fadeOut(tween(800)) },
        popEnterTransition = { fadeIn(tween(800)) },
        popExitTransition = { fadeOut(tween(800)) }
    ) {
        navigation<Route.BlogGraph>(startDestination = Route.BlogHome) {
            composable<Route.BlogHome> { entry ->
                val homeVM = koinViewModel<HomeViewModel>()
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                LaunchedEffect(Unit) { sharedVM.selectPost(null) }

                HomeScreenRoot(
                    viewModel = homeVM,
                    onPostClick = { post ->
                        sharedVM.selectPost(post)
                        navController.navigate(Route.PostDetails(post.id)) {
                            launchSingleTop = true
                        }
                    },
                    onPagesClick = { page ->
                        sharedVM.selectPage(page)
                        navController.navigate(Route.PageDetails(page.id)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<Route.PostDetails> { entry ->
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                val detailsVM = koinViewModel<PostDetailsViewModel>()
                val selected by sharedVM.selectedPost.collectAsState()

                LaunchedEffect(selected) {
                    selected?.let { detailsVM.onAction(PostDetailsActions.OnSelectedPostChange(it)) }
                }

                PostDetailsScreenRoot(
                    viewModel = detailsVM,
                    onBackClicked = { navController.navigateUp() },
                    onOpenPost = { newPost ->
                        sharedVM.selectPost(newPost)
                        navController.navigate(Route.PostDetails(newPost.id)) {
                            popUpTo<Route.PostDetails> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLabelClick = { label ->
                        navController.navigate(Route.LabeledPosts(label)) {
                            popUpTo<Route.PostDetails> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Route.PageDetails> { entry ->
//                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                val detailsVM = koinViewModel<PageDetailsViewModel>()
//                val selected by sharedVM.selectedPage.collectAsState()
//
//                LaunchedEffect(selected) {
//                    selected?.let { detailsVM.onAction(PageDetailsActions.OnSelectedPageChange(it)) }
//                }

                PageDetailsScreenRoot(
                    viewModel = detailsVM,
                    onBackClicked = { navController.navigateUp() },

                )
            }

            composable<Route.LabeledPosts> { entry ->
                val labeledVM = koinViewModel<LabeledPostsViewModel>()
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                LaunchedEffect(Unit) { sharedVM.selectPost(null) }

                LabeledScreenRoot(
                    viewModel = labeledVM,
                    onBackClick = { navController.navigateUp() },
                    onPostClick = { post ->
                        sharedVM.selectPost(post)
                        navController.navigate(Route.PostDetails(post.id)) {
                            popUpTo<Route.PostDetails> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}


@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}
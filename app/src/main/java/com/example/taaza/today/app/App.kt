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
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.taaza.today.app.BloggerApplication.Companion.isWorking
import com.example.taaza.today.bloger.ui.SelectedPostViewModel
import com.example.taaza.today.bloger.ui.home.HomeScreenRoot
import com.example.taaza.today.bloger.ui.home.HomeViewModel
import com.example.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.example.taaza.today.bloger.ui.labeled.LabeledScreenRoot
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsActions
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsScreenRoot
import com.example.taaza.today.bloger.ui.postDetails.PostDetailsViewModel
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val value = Firebase.remoteConfig.getBoolean("isWorking")
        /* ----------  NEW: global on/off switch  ---------- */
        val isWorking by isWorking.collectAsState()
        Log.d("RemoteConfig", "working: $value")
        when (isWorking) {
            true  -> AppNavigation(navController)          // normal graph
            false -> TemporarilyStoppedScreen()            // flag = false
            null -> LoadingScreen()
        }
    }
}
@Composable
private fun AppNavigation(navController: NavHostController) {
    NavHost(
            navController = navController,
            startDestination = Route.BlogGraph
        ) {
            navigation<Route.BlogGraph>(
                startDestination = Route.BlogHome
            ) {
                // Define the book list screen
                composable<Route.BlogHome>(
                    enterTransition = { fadeIn(tween( 800)) },
                    exitTransition  = { fadeOut(tween( 800)) },
                    popEnterTransition = { fadeIn(tween( 800)) },
                    popExitTransition  = { fadeOut(tween( 800)) }
                ) {
                    val viewModel = koinViewModel<HomeViewModel>()
                    val sharedViewModel =
                        it.sharedKoinViewModel<SelectedPostViewModel>(navController)

                    LaunchedEffect(true) {
                        // Reset the selected book when navigating to the book list
                        sharedViewModel.selectPost(null)
                    }
                    HomeScreenRoot(
                        viewModel = viewModel,
                        onPostClick = { post ->
                            sharedViewModel.selectPost(post)
                            navController.navigate(
                                Route.PostDetails(post.id)
                            ){ launchSingleTop = true}
                        }
                    )
                }

                composable<Route.PostDetails>(

                        enterTransition = { fadeIn(tween( 800)) },
                        exitTransition  = { fadeOut(tween( 800)) },
                        popEnterTransition = { fadeIn(tween( 800)) },
                        popExitTransition  = { fadeOut(tween( 800)) }

                        ) {
                    val sharedViewModel =
                        it.sharedKoinViewModel<SelectedPostViewModel>(navController)
                    val viewModel = koinViewModel<PostDetailsViewModel>()
                    val selectedBook by sharedViewModel.selectedPost.collectAsState()
                    LaunchedEffect(selectedBook) {
                        selectedBook?.let {
                            viewModel.onAction(
                                PostDetailsActions.OnSelectedPostChange(it)
                            )
                        }
                    }
                    PostDetailsScreenRoot(
                        viewModel = viewModel,
                        onBackClicked = {
                            navController.navigateUp()
                        },
                        onOpenPost = { newPost ->
                            sharedViewModel.selectPost(newPost)          // new data
                            navController.navigate(Route.PostDetails(newPost.id)) {
                                popUpTo<Route.PostDetails> { inclusive = true }
                                launchSingleTop =
                                    true
                            }
                        },
                        onLabelClick = {label->
                            navController.navigate(Route.LabeledPosts(label)){
                                popUpTo<Route.PostDetails> { inclusive = true }
                                launchSingleTop =
                                    true
                            }
                        }
                    )
                }
                composable<Route.LabeledPosts>(
                    enterTransition = { fadeIn(tween( 800)) },
                    exitTransition  = { fadeOut(tween( 800)) },
                    popEnterTransition = { fadeIn(tween( 800)) },
                    popExitTransition  = { fadeOut(tween( 800)) }
                ) {
                    val viewModel = koinViewModel<LabeledPostsViewModel>()
                    val sharedViewModel =
                        it.sharedKoinViewModel<SelectedPostViewModel>(navController)

                    LaunchedEffect(true) {
                        // Reset the selected book when navigating to the book list
                        sharedViewModel.selectPost(null)
                    }
                    LabeledScreenRoot(
                        viewModel = viewModel,
                        onBackClick = { navController.navigateUp() },
                        onPostClick = { post ->
                            sharedViewModel.selectPost(post)
                            navController.navigate(
                                Route.PostDetails(post.id)
                            ){
                                popUpTo<Route.PostDetails> { inclusive = true }
                                launchSingleTop =
                                    true
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
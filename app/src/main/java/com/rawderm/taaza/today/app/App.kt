package com.rawderm.taaza.today.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.app.BloggerApplication.Companion.isWorking
import com.rawderm.taaza.today.app.components.TemporarilyStoppedScreen
import com.rawderm.taaza.today.bloger.ui.SelectedPostViewModel
import com.rawderm.taaza.today.bloger.ui.home.HomeScreenRoot
import com.rawderm.taaza.today.bloger.ui.home.HomeViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsActions
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsActions
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsScreenRoot
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App(navController: NavHostController) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color.White,
            surface = Color.White
        )
    ) {
        val isWorking by isWorking.collectAsState()

        if (isWorking) {
            AppNavigation(navController)
        } else {
            TemporarilyStoppedScreen(onRetry = {})
        }
    }
}

@Composable
private fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val appUrl = context.getString(R.string.app_url) // get the string outside composable

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
                    },
                    onShortsClick = {
                        navController.navigate(Route.Shorts) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Route.PostDetails>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$appUrl/post/{postId}"
                        action = "android.intent.action.VIEW"
                    }
                )) { entry ->
                val route = entry.toRoute<Route.PostDetails>()   // navigation-compose helper
                val postId = route.postId
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                val detailsVM = koinViewModel<PostDetailsViewModel>()
                val selected by sharedVM.selectedPost.collectAsState()
                LaunchedEffect(postId) {
                    detailsVM.onAction(PostDetailsActions.OnDeepLinkArrived(postId))
                }
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
                val detailsVM = koinViewModel<PageDetailsViewModel>()

                PageDetailsScreenRoot(
                    viewModel = detailsVM,
                    onBackClicked = { navController.navigateUp() },

                    )
            }
            composable<Route.Shorts>(

                enterTransition = {
                    // Bottom → Top animation
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(100)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(100)
                    )
                }
            ) { entry ->


                val shortsViewModel = koinViewModel<ShortsViewModel>()


                ShortsScreenRoot(
                    viewModel = shortsViewModel,
                    onBackClicked = { navController.navigateUp() },
                )
            }
            composable<Route.LinkToShorts>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$appUrl/shorts/{date}"
                        action = "android.intent.action.VIEW"
                    }
                ),
                enterTransition = {
                    // Bottom → Top animation
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(100)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(100)
                    )
                }
            ) { entry ->

                val route = entry.toRoute<Route.LinkToShorts>()
                val date = route.date

                val shortsViewModel = koinViewModel<ShortsViewModel>()

                LaunchedEffect(date) {
                    if (date.isNotEmpty()) {
                        shortsViewModel.onAction(ShortsActions.OnDeepLinkArrived(date))
                        Log.d("Date", "AppNavigation: $date")
                    }
                }

                ShortsScreenRoot(
                    viewModel = shortsViewModel,
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
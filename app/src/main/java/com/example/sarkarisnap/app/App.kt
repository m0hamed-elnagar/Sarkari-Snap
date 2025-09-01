package com.example.sarkarisnap.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.sarkarisnap.bloger.ui.SelectedPostViewModel
import com.example.sarkarisnap.bloger.ui.home.HomeScreenRoot
import com.example.sarkarisnap.bloger.ui.home.HomeViewModel
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsActions
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsScreenRoot
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsViewModel
import com.plcoding.bookpedia.app.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.BlogGraph
        ) {
            navigation<Route.BlogGraph>(
                startDestination = Route.BlogHome
            ) {
                // Define the book list screen
                composable<Route.BlogHome> (
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ){
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
                            )
                        }
                    )
                }

                composable<Route.PostDetails> (

                    enterTransition = { slideInHorizontally{
                        it
                    }},
                    exitTransition = { slideOutHorizontally{it} }
                ){
                    val sharedViewModel =
                        it.sharedKoinViewModel<SelectedPostViewModel>(navController)
                    val viewModel = koinViewModel<PostDetailsViewModel>()
                    val selectedBook by sharedViewModel.selectedPost.collectAsState()
                    LaunchedEffect(selectedBook) {
                        selectedBook?.let {
                            viewModel.onAction(
                                PostDetailsActions.OnSelectedPostChange(it) )
                        }
                    }
                    PostDetailsScreenRoot(
                        viewModel = viewModel,
                        onBackClicked = {
                            navController.navigateUp()
                        }
                    )

                }
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
package com.rawderm.taaza.today.app

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.app.BloggerApplication.Companion.isWorking
import com.rawderm.taaza.today.app.components.TemporarilyStoppedScreen
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.data.PendingDeepLinkStorage
import com.rawderm.taaza.today.bloger.ui.SelectedPostViewModel
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.bloger.ui.home.HomeScreenRoot
import com.rawderm.taaza.today.bloger.ui.home.HomeViewModel
import com.rawderm.taaza.today.bloger.ui.home.components.LanguageConfirmDialog
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsActions
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsActions
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
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
        if (isWorking) AppNavigation(navController)
        else TemporarilyStoppedScreen(onRetry = {})
    }
}

@Composable
private fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val appUrl = context.getString(R.string.app_url)
    val languageManager = koinInject<LanguageManager>()
    val currentLang by languageManager.currentLanguage.collectAsState()
    val homeVM: HomeViewModel = koinViewModel<HomeViewModel>()
    val shortsViewModel = koinViewModel<ShortsViewModel>()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pendingDeepLinkData by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        PendingDeepLinkStorage.consume(context)?.let { (type, id, lang) ->
            if (type.isNullOrEmpty())return@LaunchedEffect
            when (type) {
                "post" -> {
                    Log.d("deeplink", "AppNavigation: "+ id+lang)
                    navController.navigate(Route.PostDetails(lang, id)) {
                        popUpTo<Route.BlogGraph> { inclusive = true }
                        launchSingleTop = true
                    }
                }
                "short" -> {
                    if (id.isBlank()) return@let
                    Log.d("deeplink", "AppNavigation: "+ id+lang)

                    shortsViewModel.onAction(ShortsActions.OnGetShortsByDate(id, lang))
                    homeVM.onAction(HomeActions.OnTabSelected(2))
                }
                else ->{}
            }
        }
    }

    // Handle language switching from deep link
//    LaunchedEffect(showLanguageDialog) {
//        if (!showLanguageDialog && pendingDeepLinkData != null) {
//            val (targetLang, postId) = pendingDeepLinkData!!
//            // Navigate to the post with the new language
//            navController.navigate(Route.PostDetails(targetLang, postId)) {
//                popUpTo<Route.BlogGraph> { inclusive = false }
//                launchSingleTop = true
//            }
//            pendingDeepLinkData = null
//        }
//    }

//    if (showLanguageDialog && pendingDeepLinkData != null) {
//        val (targetLang, _) = pendingDeepLinkData!!
//        LanguageConfirmDialog(
//            modifier = Modifier,
//            context = context,
//            languageManager = languageManager,
//            scope = scope,
//            requestedLang = targetLang,
//            onAccept = {
//                showLanguageDialog = false
//                scope.launch {
//                    pendingDeepLinkData?.let { (_, postId) ->
//                        // Navigate to the post with current language
//                        navController.navigate(Route.PostDetails(currentLang, postId)) {
//                            popUpTo<Route.BlogGraph> { inclusive = true }
//                            launchSingleTop = true
//                        }
//                        pendingDeepLinkData = null   }
//
//                }
//            },
//            onDecline = { showLanguageDialog = false
//                pendingDeepLinkData = null}
//        )
//    }

    NavHost(
        navController = navController,
        startDestination = Route.BlogGraph,
        enterTransition = { fadeIn(tween(800)) },
        exitTransition = { fadeOut(tween(800)) },
        popEnterTransition = { fadeIn(tween(800)) },
        popExitTransition = { fadeOut(tween(800)) }
    ) {

        navigation<Route.BlogGraph>(
            startDestination = Route.BlogHome::class,
            deepLinks = listOf(
                navDeepLink { uriPattern = context.getString(R.string.app_url) + "/" }
            )
        ) {
            composable<Route.BlogHome>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$appUrl/{lang}/shorts/{date}"
                        action = "android.intent.action.VIEW"
                    }
                )
                )
            { entry ->
                val route = entry.toRoute<Route.BlogHome>()
                var date = route.date
                val lang = route.lang

                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)


                var showLangDialog by remember { mutableStateOf(false) }
                var pendingShortsDate by remember { mutableStateOf<String?>(null) }


                LaunchedEffect(Unit) { sharedVM.selectPost(null) }
                LaunchedEffect(date) {
                    if (!date.isNullOrBlank()) {
                        shortsViewModel.onAction(ShortsActions.OnGetShortsByDate(date!!, lang))
                        homeVM.onAction(HomeActions.OnTabSelected(2))
                        Log.d("Date", "AppNavigation: $date")
                        date = null


                    }
                }
                HomeScreenRoot(
                    viewModel = homeVM,
                    shortsViewModel = shortsViewModel,
                    onPostClick = { post ->
                        sharedVM.selectPost(post)
                        navController.navigate(Route.PostDetails(currentLang, post.id)) {
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
            composable<Route.PostDetails>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "$appUrl/{lang}/post/{postId}"
                        action = "android.intent.action.VIEW"
                    }
                )) { entry ->
                val route = entry.toRoute<Route.PostDetails>()
                val lang = route.lang
                val postId = route.postId
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)
                val detailsVM = koinViewModel<PostDetailsViewModel>()
                val selected by sharedVM.selectedPost.collectAsState()


                LaunchedEffect(lang, currentLang, postId) {
                    if (lang != currentLang) {
                        // Show language confirmation dialog
                        pendingDeepLinkData = lang to postId
                        showLanguageDialog = true
                    } else {
                        // Language matches, proceed normally
                        detailsVM.onAction(PostDetailsActions.OnDeepLinkArrived(lang, postId))
                    }
                }

                LaunchedEffect(selected) {
                    selected?.let { detailsVM.onAction(PostDetailsActions.OnSelectedPostChange(it)) }
                }

                PostDetailsScreenRoot(
                    viewModel = detailsVM,
                    onBackClicked = { navController.navigateUp() },
                    onOpenPost = { newPost ->
                        sharedVM.selectPost(newPost)
                        navController.navigate(Route.PostDetails(currentLang, newPost.id)) {
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
                    onBackClicked = { navController.navigateUp() }
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
                        navController.navigate(Route.PostDetails(currentLang, postId = post.id)) {
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
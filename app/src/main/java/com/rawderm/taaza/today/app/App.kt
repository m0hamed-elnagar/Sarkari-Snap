package com.rawderm.taaza.today.app

import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.setValue
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
import com.rawderm.taaza.today.bloger.data.DeepLinkHandler
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.data.PendingDeepLinkStorage
import com.rawderm.taaza.today.bloger.ui.SelectedPostViewModel
import com.rawderm.taaza.today.bloger.ui.articleDetails.PostDetailsActions
import com.rawderm.taaza.today.bloger.ui.articleDetails.PostDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.articleDetails.PostDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.bloger.ui.home.HomeScreenRoot
import com.rawderm.taaza.today.bloger.ui.home.HomeViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsScreenRoot
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.quiks.QuiksActions
import com.rawderm.taaza.today.bloger.ui.quiks.QuiksViewModel
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsActions
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

data class DeepLink(
    val type: String,   // "post", "short", "quiks", ...
    val id: String,     // date / post-id / short-id
    val lang: String    // "en", "hi", ...
) {
    /** Returns true if the link contains the minimum required data. */
    val isValid: Boolean
        get() = type.isNotBlank() && id.isNotBlank() && lang.isNotBlank()
}

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
    val quikViewModel = koinViewModel<QuiksViewModel>()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pendingDeepLinkData by remember { mutableStateOf<Pair<String, String>?>(null) }
//    LaunchedEffect(Unit) {
//        PendingDeepLinkStorage.consume(context)?.let { (type, id, lang) ->
//        Log.d("deeplink", "AppNavigation: "+ type+id+lang)
//
//            if (type.isNullOrEmpty())return@LaunchedEffect
//            when (type) {
//                "post" -> {
//                    Log.d("deeplink", "AppNavigation: "+ id+lang)
//                    navController.navigate(Route.PostDetails(lang, id)) {
//                        popUpTo<Route.BlogGraph> { inclusive = true }
//                        launchSingleTop = true
//                    }
//                }
//                "short" -> {
//                    Log.d("deeplink", "AppNavigation: "+ id+lang)
//
//                    shortsViewModel.onAction(ShortsActions.OnGetShortsByDate(id, lang))
//                    homeVM.onAction(HomeActions.OnTabSelected(2))
//                }
//                "quiks"->{
//                    Log.d("deeplink", "AppNavigation: "+ id+lang)
//
//                    shortsViewModel.onAction(ShortsActions.OnGetShortsByDate(id, lang))
//                    homeVM.onAction(HomeActions.OnTabSelected(1))
//
//                }
//
//                else ->{
////                    homeVM.onAction(HomeActions.OnTabSelected(1))
//                    Log.d("deeplink", "AppNavigation: "+ id+lang)
//
//                }
//
//            }
//        }
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
                        uriPattern = "$appUrl/{lang}/{type}/{date}"
                        action = "android.intent.action.VIEW"
                    },
//                    navDeepLink {
//                        uriPattern = "$appUrl/{lang}/{type}/{date}"
//                        action = "android.intent.action.VIEW"
//                    },
                )
            )
            { entry ->
                val route = entry.toRoute<Route.BlogHome>()
                var date = route.date
                val lang = route.lang
                val type = entry.toRoute<Route.BlogHome>().type
                val sharedVM = entry.sharedKoinViewModel<SelectedPostViewModel>(navController)


                var showLangDialog by remember { mutableStateOf(false) }
                var pendingShortsDate by remember { mutableStateOf<String?>(null) }
                val activity = LocalActivity.current
                LaunchedEffect(Unit) { sharedVM.selectPost(null) }
LaunchedEffect(date) {
    if (!date.isNullOrBlank() && !DeepLinkHandler.consumed) {
        DeepLinkHandler.consumed = true  // ✅ Prevent future triggers

        when (type) {
            "shorts" -> {
                shortsViewModel.onAction(ShortsActions.OnGetShortsByDate(date!!, lang))
                homeVM.onAction(HomeActions.OnTabSelected(2))
            }
            "quiks" -> {
                quikViewModel.onAction(QuiksActions.OnGetShortsByDate(date!!, lang))
                homeVM.onAction(HomeActions.OnTabSelected(1))
            }
            else -> homeVM.onAction(HomeActions.OnTabSelected(0))
        }

        Log.d("DeepLink", "Handled deep link: type=$type, date=$date, lang=$lang")

        activity?.intent = Intent(activity.intent).apply { data = null }
        date = null
    } else if (DeepLinkHandler.consumed) {
        Log.d("DeepLink", "Skipping already handled deep link")
    }
}
                HomeScreenRoot(
                    viewModel = homeVM,
                    shortsViewModel = shortsViewModel,
                    quikViewModel = quikViewModel,
                    onPostClick = { post ->
                        sharedVM.selectPost(post)
                        navController.navigate(Route.PostDetails(currentLang, post.id)) {
                            launchSingleTop = true
                        }
                    },
                    onQuickClick = { postId ->
                        navController.navigate(Route.PostDetails(currentLang, postId)) {
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
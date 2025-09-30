//package com.rawderm.taaza.today.bloger.ui.shorts.components
//
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//import com.rawderm.taaza.today.bloger.ui.shorts.YouTubePlayerState
//import kotlinx.coroutines.flow.Flow
//
//@Composable
//fun TikTokYouTubePlayer(
//    videoId: String,
//    modifier: Modifier = Modifier,
//    playerEvent: Flow<PlayerEvent>,
//    onReady: (YouTubePlayer) -> Unit = {},
//    onStateChange: (YouTubePlayerState) -> Unit = {}
//) {
//    val context = LocalContext.current
//    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
//    var ytPlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
//    var playerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
//
//    // ---- lifecycle aware ----
//    DisposableEffect(Unit) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_PAUSE -> ytPlayer?.pause()
//                else -> Unit
//            }
//        }
//        lifecycleOwner.addObserver(observer)
//        onDispose { lifecycle.removeObserver(observer) }
//    }
//
//    // ---- react to events ----
//    LaunchedEffect(Unit) {
//        playerEvent.collect { event ->
//            when (event) {
//                is PlayerEvent.Play -> ytPlayer?.loadVideo(videoId, 0f)
//                PlayerEvent.Pause -> ytPlayer?.pause()
//            }
//        }
//    }
//
//    AndroidView(
//        factory = {ctx->
//            YouTubePlayerView(ctx).also { view ->
//                view.enableAutomaticInitialization = false
//                val opts = IFramePlayerOptions.Builder(ctx)
//                    .controls(0)
//                    .rel(0)
//                    .build()
//                view.initialize(
//                    object : AbstractYouTubePlayerListener() {
//                        override fun onReady(player: YouTubePlayer) {
//                            ytPlayer = player
//                            onReady(player)
//                        }
//
//                        override fun onStateChange(
//                            player: YouTubePlayer,
//                            state: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener.State
//                        ) {
//                            onStateChange(YouTubePlayerState.valueOf(state.name))
//                        }
//                    },
//                    opts
//                )
//                playerView = view
//            }
//        },
//        modifier = modifier
//    )
//
//    DisposableEffect(Unit) {
//        onDispose {
//            playerView?.release()
//        }
//    }
//}
package com.rawderm.taaza.today.bloger.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubeVideoPlayer(
    videoId: String,
    autoPlay: Boolean,            // we now observe this
    modifier: Modifier = Modifier  // let caller centre / size us
) {
    val context = LocalContext.current
    var ytPlayer by remember { mutableStateOf<YouTubePlayer?>(null) }

    val youTubePlayerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            setPadding(0, 0, 0, 0)

            // Force the WebView to scale content to fit
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
    ) {
        AndroidView(
            factory = { context ->
                youTubePlayerView.apply {
                    val playerOptions = IFramePlayerOptions.Builder(context = context)
                        .controls(0) // Hide controls for Shorts-like experience
                        .fullscreen(0)
                        .rel(0)
                        .ivLoadPolicy(3)
                        .build()

                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            if (autoPlay) {
                                // Load and autoplay the video
                                youTubePlayer.loadVideo(videoId, 0f)
                            } else {
                                // Just cue th video without autoplay
                                youTubePlayer.cueVideo(videoId, 0f)
                            }
                        }
                    })

                    initialize(object : AbstractYouTubePlayerListener() {}, true, playerOptions)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }

    DisposableEffect(videoId) {
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
                if (autoPlay) youTubePlayer.loadVideo(videoId, 0f)
                else           youTubePlayer.cueVideo(videoId, 0f)
            }
        }
        val options = IFramePlayerOptions.Builder(context)
            .controls(0).rel(0).ivLoadPolicy(3).build()
        youTubePlayerView.initialize(listener, true, options)

        onDispose {
            youTubePlayerView.release()
            ytPlayer = null
        }
    }
    LaunchedEffect(autoPlay) {
        ytPlayer?.let { if (autoPlay) it.play() else it.pause() }
    }
}
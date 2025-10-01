package com.rawderm.taaza.today.bloger.ui.components

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubeShortsPlayer(
    videoIds: List<String>,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,          // NEW
    onVideoEnd: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentIndex by remember { mutableIntStateOf(0) }

    val listener = remember(autoPlay) {
        object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (autoPlay) {          // <-- only load if we really want to play
                    try {
                        youTubePlayer.loadVideo(videoIds[currentIndex], 0f)
                    } catch (e: Exception) {
                        Log.e("YouTubeShorts", "Failed to load video: ${e.message}")
                    }
                }
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                if (state == PlayerConstants.PlayerState.ENDED) {
                    if (currentIndex < videoIds.size - 1) {
                        currentIndex++
                        youTubePlayer.loadVideo(videoIds[currentIndex], 0f)
                    } else {
                        onVideoEnd()
                    }
                }
            }
        }
    }

    val playerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            setPadding(0, 0, 0, 0)

            // Force the WebView to scale content to fit
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    DisposableEffect(Unit) {
        val playerOptions = IFramePlayerOptions.Builder(context)
            .controls(0)
            .fullscreen(0)
            .rel(0)
            .ivLoadPolicy(3)
            .build()

        playerView.addYouTubePlayerListener(listener)
        lifecycleOwner.lifecycle.addObserver(playerView)

        playerView.initialize(object : AbstractYouTubePlayerListener() {}, true, playerOptions)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerView)
            playerView.removeYouTubePlayerListener(listener)
            playerView.release()
        }
    }

    /* when this page becomes the settled one -> start playback */
    LaunchedEffect(autoPlay) {
        if (autoPlay && videoIds.isNotEmpty()) {
            playerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    try {
                        youTubePlayer.loadVideo(videoIds[currentIndex], 0f)
                    } catch (e: Exception) {
                        Log.e("YouTubeShorts", "Failed to switch video: ${e.message}")
                    }
                }
            })
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
    ) {
        AndroidView(
            factory = { playerView },
            modifier = Modifier .fillMaxWidth()
        )
    }
}
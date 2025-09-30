package com.rawderm.taaza.today.bloger.ui.home.components

import android.util.Log
import android.webkit.WebView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.rawderm.taaza.today.bloger.data.mappers.extractBloggerVideoToken

@OptIn(UnstableApi::class)
@Composable
fun NativeBloggerVideo(
    modifier: Modifier = Modifier,
    videoUrl: String
) {
    val context = LocalContext.current
    val hardcodedVideoUrl = "https://blogger.googleusercontent.com/v/faeec605ecb48d94"

    // 3. build ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()
            .apply {
                // Add error listener for debugging
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("ExoPlayer", "Player error: ${error.errorCodeName}", error)
                        Log.e("ExoPlayer", "Error message: ${error.message}")
                        when (error.errorCode) {
                            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> {
                                Log.e("ExoPlayer", "HTTP Error - URL might be invalid: $videoUrl")
                            }
                            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                                Log.e("ExoPlayer", "Video file not found: $videoUrl")
                            }
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                                Log.e("ExoPlayer", "Network connection failed")
                            }
                            PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> {
                                Log.e("ExoPlayer", "Invalid content type - might need different approach")
                            }
                        }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> Log.d("ExoPlayer", "State: BUFFERING")
                            Player.STATE_READY -> {
                                Log.d("ExoPlayer", "State: READY - Video loaded successfully")
                                Log.d("ExoPlayer", "Video duration: ${duration}ms")
                            }
                            Player.STATE_ENDED -> Log.d("ExoPlayer", "State: ENDED")
                            Player.STATE_IDLE -> Log.d("ExoPlayer", "State: IDLE")
                        }
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        Log.d("ExoPlayer", "Available tracks: ${tracks.groups.size}")
                        tracks.groups.forEachIndexed { index, group ->
                            Log.d("ExoPlayer", "Track $index: ${group.mediaTrackGroup.length} ")
                        }}
                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        Log.d("ExoPlayer", "Loading: $isLoading")
                    }
                })
            }
    }
    val videoUrl ="https://video.blogger.com/video-play.mp4?contentId=faeec605ecb48d94"

    val defaultDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
        .setDefaultRequestProperties(
            mapOf(
                "Referer" to "https://taazatodayhindi.blogspot.com/" // your blog
            )
        )

    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
        .createMediaSource(MediaItem.fromUri(videoUrl))


    LaunchedEffect(videoUrl) {
        Log.d("ExoPlayer", "Attempting to load video URL: $videoUrl")
        try {
            if (videoUrl.isBlank()) {
                Log.e("ExoPlayer", "Video URL is blank")
                return@LaunchedEffect
            }

            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            Log.d("ExoPlayer", "Media item set successfully")
        } catch (e: Exception) {
            Log.e("ExoPlayer", "Error setting up media item", e)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("ExoPlayer", "Releasing player")
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
 fun YouTubeCard(videoId: String) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                keepScreenOn = true
                settings.mediaPlaybackRequiresUserGesture = false
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                loadUrl(
                    "https://www.youtube.com/embed/$videoId"
//                    "?rel=0&autoplay=1&mute=1&playsinline=1&controls=0&loop=1"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)   // 9:16 vertical video
    )
}
private fun buildUrl(id: String) =
    "https://www.youtube.com/embed/$id" +
            "?rel=0&autoplay=1&mute=1&playsinline=1&controls=0&loop=1"
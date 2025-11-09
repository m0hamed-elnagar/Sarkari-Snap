package com.rawderm.taaza.today.bloger.ui.components.ads

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd

@Composable
fun NativeScreen(
    modifier: Modifier = Modifier,
    nativeAdUnitID: String = "ca-app-pub-7395572779611582/3507915065",
    testNativeAdUnitID: String = "ca-app-pub-3940256099942544/2247696110",
    onAdResult: (loaded: Boolean) -> Unit = {}
) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        isLoading = true
        hasError = false

        loadNativeAd(
            context = context,
            nativeAdUnitID = nativeAdUnitID,
            onAdLoaded = { ad ->
                // Handle the native ad being loaded.
                if (!isDisposed) {
                    nativeAd = ad
                    isLoading = false
                    Log.d("NATIVE_AD", "Ad loaded successfully in NativeScreen")
                    onAdResult(true)
                } else {
                    // Destroy the native ad if loaded after the screen is disposed.
                    ad.destroy()
                }
            },
            onAdFailed = {
                if (!isDisposed) {
                    isLoading = false
                    hasError = true
                    Log.e("NATIVE_AD", "Failed to load native ad in NativeScreen")
                    onAdResult(false)
                }
            }
        )
        // Destroy the native ad to prevent memory leaks when we dispose of this screen.
        onDispose {
            isDisposed = true
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(
                            "Loading Ad...",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            hasError -> {
                // Error state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Ad not available",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            nativeAd != null -> {
                // Success state
                DisplayNativeAdView(nativeAd!!, modifier)
            }
        }
    }
}
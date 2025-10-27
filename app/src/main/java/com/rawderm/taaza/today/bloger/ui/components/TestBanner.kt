package com.rawderm.taaza.today.bloger.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAd(
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    modifier: Modifier = Modifier,
    adView: AdView?=null

) {

    val context = LocalContext.current
    val adViewState = remember {
        adView ?:AdView(context).apply {
            this.adUnitId = adUnitId
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360)
            setAdSize(adSize)
        }
    }
        var isLoaded by remember { mutableStateOf(false) }

    // 3. listener updates the flag
    LaunchedEffect(adViewState) {
        adViewState.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("BANNER_AD", "Ad loaded")
                isLoaded = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("BANNER_AD", "Ad failed: ${loadAdError.message}")
                isLoaded = false // keep hidden on failure
            }
        }

        // first (and only) load request
        adViewState.loadAd(AdRequest.Builder().build())
    }

    if (isLoaded) {
        AndroidView(
            factory = { adViewState },
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}
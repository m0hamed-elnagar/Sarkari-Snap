package com.rawderm.taaza.today.bloger.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.BannerAd



@Composable
fun TestBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    ) {
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                this.adUnitId = adUnitId

                // compute width in dp
                val dm = ctx.resources.displayMetrics
                val adWidthPixels = dm.widthPixels.toFloat()
                val adWidth = (adWidthPixels / dm.density).toInt()
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth)
                )

                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
@Composable
fun BannerAd(
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    modifier: Modifier = Modifier,
    adView: AdView?=null

) {

    val context = LocalContext.current
    val adView = remember {
        adView?:
        AdView(context).apply {
            this.adUnitId = adUnitId
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360)
            setAdSize(adSize)

            // Add ad listener for debugging
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.d("BANNER_AD", "Ad loaded successfully in TestBanner2")
                }
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("BANNER_AD", "Ad failed to load: ${loadAdError.message}")
                }
            }
        }
    }

    LaunchedEffect(adView) {
        Log.d("BANNER_AD", "Loading ad in TestBanner2")
        adView.loadAd(AdRequest.Builder().build())
    }

    AndroidView(
        factory = { adView },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}
@Composable
fun TestBanner2(modifier: Modifier = Modifier) {
    val context =LocalContext.current
    val adView =  AdView(context)
    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360)
    adView.setAdSize(adSize)
    LaunchedEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
    }



    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        Box(modifier = modifier.fillMaxWidth()) { BannerAd(adView, modifier) }
    }
}
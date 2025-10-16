package com.rawderm.taaza.today.bloger.ui.components.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

fun loadNativeAd(
    context: Context,
    onAdLoaded: (NativeAd) -> Unit,
    nativeAdUnitID: String ,
    onAdFailed: () -> Unit = {}
) {
    val TAG = "NativeAd"
    val videoOptions = VideoOptions.Builder()
        .setStartMuted(true)            // mute by default
        .build()
    val nativeAdOptions =
        NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)  // allow video
            .setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
            .build()
    val adLoader = AdLoader.Builder(context, nativeAdUnitID).withNativeAdOptions(nativeAdOptions)
        .forNativeAd { nativeAd ->
            onAdLoaded(nativeAd)
        }
        .withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "Native ad failed to load: ${error.message} - Code: ${error.code}")
                    onAdFailed()
                }

                override fun onAdLoaded() {
                    Log.d(TAG, "Native ad was loaded.")
                }

                override fun onAdImpression() {
                    Log.d(TAG, "Native ad recorded an impression.")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "Native ad was clicked.")
                }
            }
        )        .withNativeAdOptions(nativeAdOptions)   // <── important

        .build()
    adLoader.loadAd(AdRequest.Builder().build())
}

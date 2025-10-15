package com.rawderm.taaza.today.bloger.ui.components.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

fun loadNativeAd(
    context: Context,
    onAdLoaded: (NativeAd) -> Unit,
    nativeAdUnitID: String ,
    onAdFailed: () -> Unit = {}
) {
    val TAG = "NativeAd"

    val adLoader = AdLoader.Builder(context, nativeAdUnitID)
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
        )
        .build()
    adLoader.loadAd(AdRequest.Builder().build())
}

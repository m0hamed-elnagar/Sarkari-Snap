package com.rawderm.taaza.today.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.rawderm.taaza.today.bloger.ui.components.TestBanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


class MainActivity : ComponentActivity() {
    var interstitialAd: InterstitialAd? = null
    val TAG = "Ads"

    //    @OptIn(ExperimentalAtomicApi::class)
//    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
//    private var adView: AdView? = null
//    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Black.toArgb(),
                darkScrim = Color.Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Black.toArgb(),
                darkScrim = Color.Black.toArgb()
            )
        )
        // 2.  SDK INIT


        // 1.  TEST DEVICES  (copied from BannerExample)
//        val testDeviceIds = listOf(
//            AdRequest.DEVICE_ID_EMULATOR,
//            "BEBE208E5C5E5C5E5C5E5C5E5C5E5C5"   // replace with your real device hash
//        )
//        val requestConfig = RequestConfiguration.Builder()
//            .setTestDeviceIds(testDeviceIds)
//            .build()
//        MobileAds.setRequestConfiguration(requestConfig)
        CoroutineScope(Dispatchers.Main).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) { initializationStatus ->
                Log.d("AdMob", "SDK init done: ${initializationStatus.adapterStatusMap}")
                loadInterstitialAd()
                loadNativeAd()
            }
        }

        setContent {


            val navController = rememberNavController()
            App(navController)
        }


    }

    fun loadNativeAd() {
        val context = this@MainActivity
        // It is recommended to call AdLoader.Builder on a background thread.
        CoroutineScope(Dispatchers.Main).launch {
            val adLoader =
                AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110" )
                    .forNativeAd { nativeAd ->
                    Log.d("Ads"," The native ad loaded successfully. You can show the ad.")
                    }
                    .withAdListener(
                        object : AdListener() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                // The native ad load failed. Check the adError message for failure reasons.
                            }
                        }
                    )
                    // Use the NativeAdOptions.Builder class to specify individual options settings.
                    .withNativeAdOptions(NativeAdOptions.Builder().build())
                    .build()

        }
    }

    fun showAd() {
        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when fullscreen content failed to show.
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdClicked() {
                    // Called when ad is clicked.
                    Log.d(TAG, "Ad was clicked.")
                }
            }
        interstitialAd?.show(this@MainActivity)
    }

    fun loadInterstitialAd() {
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("Ads", "Ad was loaded.")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Ads", adError.message)
                    interstitialAd = null
                }
            },
        )
    }
}
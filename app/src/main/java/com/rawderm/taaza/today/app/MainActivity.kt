package com.rawderm.taaza.today.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.rawderm.taaza.today.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE // Consider using FLEXIBLE for better UX

    private val updateCheckedThisSession = AtomicBoolean(false)

    // Fixed update launcher - proper implementation
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> Timber.d("Update started successfully")
            RESULT_CANCELED -> Timber.d("Update cancelled by user")
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED ->
                Timber.e("Update failed with code: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Black.toArgb())
        )
        MobileAds.initialize(this@MainActivity) { initializationStatus ->
            Log.d("AdMob", "SDK init done: ${initializationStatus.adapterStatusMap}")
            loadNativeAd()
        }
        setContent {
            App(rememberNavController())
        }
        appUpdateManager = AppUpdateManagerFactory.create(this)

        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }

        // Check for updates with proper error handling
        lifecycleScope.launch {
            if (!updateCheckedThisSession.getAndSet(true)) {
                checkForAppUpdate()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { info ->
                when {
                    info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                            && updateType == AppUpdateType.IMMEDIATE->  startUpdateFlow(info)

                    info.installStatus() == InstallStatus.DOWNLOADED -> restartSnackbar()
                }
            }
    }
    private val installStateUpdatedListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                restartSnackbar()

            }
            InstallStatus.INSTALLED -> {
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
            else -> { /* Handle other states if needed */ }
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val allowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (available && allowed) startUpdateFlow(info)
        }.addOnFailureListener { Timber.e(it, "Update check failed") }
    }

    private fun startUpdateFlow(info: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                info,
                updateLauncher,
                AppUpdateOptions.newBuilder(updateType)
                    .setAllowAssetPackDeletion(true)
                    .build()
            )
        } catch (e: Exception) {
            Timber.e(e, "Cannot start update flow")
        }
    }

    private fun restartSnackbar() {
        Toast.makeText(
            this,
            getString(R.string.download_successful_restart),
            Toast.LENGTH_LONG
        ).show()
        lifecycleScope.launch {
            delay(5_000)
            appUpdateManager.completeUpdate()
        }
    }

    private fun loadNativeAd() {
        AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                Log.d("Ads", " The native ad loaded successfully. You can show the ad.")
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
            .loadAd(AdRequest.Builder().build())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}
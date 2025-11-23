package com.rawderm.taaza.today.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
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
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.DeepLinkHandler
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.ui.components.LanguageConfirmDialog
import com.rawderm.taaza.today.bloger.ui.components.LanguagePickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE // Consider using FLEXIBLE for better UX

    private val updateCheckedThisSession = AtomicBoolean(false)

    private val languageDataStore by inject<LanguageDataStore>()
    private val languageManager by inject<LanguageManager>()
    private lateinit var navController: NavHostController


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

        // 1. Parse the link that started the app
        val deepLinkLang = intent?.data?.pathSegments?.firstOrNull {
            it.length == 2          // "en", "hi", "es" ...
        }
        val currentLanguage = runBlocking { languageDataStore.getLanguage() }


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Black.toArgb())
        )
        MobileAds.initialize(this@MainActivity) { initializationStatus ->
            Log.d("AdMob", "SDK init done: ${initializationStatus.adapterStatusMap}")
            loadNativeAd()
        }
        setContent {
            var showLangGate by remember { mutableStateOf(true) }   // 1. start with gate open
            var showLanguagePicker by remember { mutableStateOf(false) } // For unconditional language picker
            requestNotificationPermission()

            // Check if this is first launch to show language picker
            LaunchedEffect(Unit) {
                val isFirstLaunch = languageDataStore.isFirstLaunch()
                if (isFirstLaunch) {
                    showLanguagePicker = true
                }
            }

            if (showLangGate) {
                LanguageGate(                                       // 2. dialog only
                    deepLinkLang = deepLinkLang,
                    currentLang = currentLanguage,
                    languageManager = languageManager,
                    onPassed = { showLangGate = false }            // 3. close gate
                )
            } else if (showLanguagePicker) {
                // Show language picker dialog unconditionally
                ShowLanguagePickerDialog(
                    languageManager = languageManager,
                    onLanguageSelected = {
                        showLanguagePicker = false
                    }
                )
            } else {
                navController = rememberNavController()
                App(navController)                                 // 4. real app
            }
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

    @Composable
    private fun ShowLanguagePickerDialog(
        languageManager: LanguageManager,
        onLanguageSelected: () -> Unit
    ) {

        Box(
            Modifier
                .fillMaxSize()
                .background(White)
        ) {
            LanguagePickerDialog(
                Modifier
                    .fillMaxSize()
                    .background(White),
                languageManager,
                rememberCoroutineScope()
            ) {}
            onLanguageSelected()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        DeepLinkHandler.consumed = false

        intent.let { newIntent ->
            // 1. make it the intent that every lifecycle method will see from now on
            setIntent(newIntent)
            DeepLinkHandler.consumed = false

            Log.d("DeepLink", "New intent: $newIntent")
            // 3. let Navigation handle the deep link
            if (this::navController.isInitialized) {
                navController.handleDeepLink(intent)
            } else {
                Log.w("DeepLink", "NavController not initialized yet, skipping deep link")
            }
        }
    }

    @Composable
    private fun LanguageGate(
        deepLinkLang: String?,
        currentLang: String,
        languageManager: LanguageManager,
        onPassed: () -> Unit
    ) {
        // nothing to do → skip gate immediately
        if (deepLinkLang == null || deepLinkLang == currentLang) {
            LaunchedEffect(Unit) { onPassed() }
            return
        }

        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()
        LanguageConfirmDialog(
            modifier = Modifier,
            context = ctx,
            languageManager = languageManager,
            scope = scope,
            requestedLang = deepLinkLang,
            onAccept = {
                onPassed()
            },
            onDecline = {
                onPassed()
                DeepLinkHandler.consumed = true
            }
        )
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            when {
                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                        && updateType == AppUpdateType.IMMEDIATE -> startUpdateFlow(info)

                info.installStatus() == InstallStatus.DOWNLOADED -> restartSnackbar()
            }
        }
    }

    private val installStateUpdatedListener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADED -> restartSnackbar()
                InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                    installStateUpdatedListener
                )

                else -> Unit
            }
        }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                val avail = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val allowed = when (updateType) {
                    AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                    AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                    else -> false
                }
                if (avail && allowed) startUpdateFlow(info)
            }
            .addOnFailureListener { Timber.e(it, "Update check failed") }
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
        Toast.makeText(this, getString(R.string.download_successful_restart), Toast.LENGTH_LONG)
            .show()
        lifecycleScope.launch { delay(5_000); appUpdateManager.completeUpdate() }
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
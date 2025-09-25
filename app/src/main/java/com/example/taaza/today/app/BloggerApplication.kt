package com.example.taaza.today.app


import android.app.Application
import android.util.Log
import com.example.taaza.today.BuildConfig
import com.example.taaza.today.di.initKoin
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext


class BloggerApplication : Application() {
    companion object {
        private val _isWorking = MutableStateFlow(true)   // always true until we know better
        val isWorking: StateFlow<Boolean> get() = _isWorking.asStateFlow()
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()

        initKoin { androidContext(this@BloggerApplication) }
        FirebaseApp.initializeApp(this)

        setupAnalytics()
        setupCrashlytics()
        keepTryingRemoteConfig()

    }

    private fun setupAnalytics() {
        val analytics = Firebase.analytics
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        // Example of adding useful params globally
        analytics.setUserProperty("build_type", BuildConfig.BUILD_TYPE)
    }

    private fun setupCrashlytics() {
        val crashlytics = FirebaseCrashlytics.getInstance()

        // Use Firebase Installation ID as a safe unique identifier
        FirebaseInstallations.getInstance().id.addOnSuccessListener { fid ->
            crashlytics.setUserId(fid)
        }

        crashlytics.log("App started")
        crashlytics.setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        crashlytics.setCustomKey("version", BuildConfig.VERSION_NAME)

    }

    private fun keepTryingRemoteConfig() {
        val config = Firebase.remoteConfig
        config.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
        )
        config.setDefaultsAsync(mapOf("isWorking" to true))

        // 1. fastest cached value
        config.activate()

        // 2. listen for *server* updates (no extra fetch calls)
        config.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(update: ConfigUpdate) {
                if ("isWorking" in update.updatedKeys) {
                    config.activate().addOnCompleteListener { publish() }
                }
            }

            override fun onError(e: FirebaseRemoteConfigException) {
                Log.e("RC", "real-time listener error", e)
            }
        })

        // 3. single fetch (cold start) – exactly like the working code
        config.fetchAndActivate().addOnCompleteListener { publish() }
    }

    private fun publish() {
        val v = Firebase.remoteConfig.getBoolean("isWorking")
        _isWorking.value = v
        Log.d("RC", "activated isWorking=$v")
    }
}

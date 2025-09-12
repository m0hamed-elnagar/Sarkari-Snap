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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext


class BloggerApplication : Application() {
    companion object {
        // Nullable: null = loading, false = error, true = working
        private val _isWorking = MutableStateFlow<Boolean?>(null)
        val isWorking: StateFlow<Boolean?> get() = _isWorking.asStateFlow()
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // Start dependency injection
        initKoin { androidContext(this@BloggerApplication) }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setupAnalytics()
        setupCrashlytics()
        setupRemoteConfig()
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

    private fun setupRemoteConfig() {
        val config = FirebaseRemoteConfig.getInstance()

        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
            .build()

        config.setConfigSettingsAsync(settings)

        // Default values
        config.setDefaultsAsync(mapOf("isWorking" to false))

        // Fetch + Activate
        config.fetchAndActivate()
            .addOnCompleteListener { task ->
                appScope.launch {
                    if (task.isSuccessful) {
                        val value = config.getBoolean("isWorking")
                        _isWorking.value = value
                        Log.d("RemoteConfig", "Fetch succeeded → isWorking=$value")
                    } else {
                        _isWorking.value = false
                        Log.w("RemoteConfig", "Fetch failed → fallback to default")
                    }
                }
            }
    }
}
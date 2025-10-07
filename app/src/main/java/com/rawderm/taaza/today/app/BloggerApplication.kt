package com.rawderm.taaza.today.app


import android.app.Application
import android.content.Context
import android.util.Log
import android.webkit.WebView
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
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.di.initKoin
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.koin.androidContext


class BloggerApplication : Application() {
    companion object {
        private val _isWorking = MutableStateFlow(true)   // always true until we know better
        val isWorking: StateFlow<Boolean> get() = _isWorking.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()

        initKoin { androidContext(this@BloggerApplication) }
        FirebaseApp.initializeApp(this)

        setupAnalytics()
        setupCrashlytics()
        keepTryingRemoteConfig()
        Lingver.init(this, "hi")          // fallback for very first install

        // 2. Read whatever is stored (or fallback) and **apply** it
        val code = Lingver.getInstance().getLanguage()
        Lingver.getInstance().setLocale(this, code)
        try {
            WebView(this).destroy()
        } catch (_: Throwable) { /* WebView not available */ }

        // 3. restore locale again (WebView just reset it)
        Lingver.getInstance().setLocale(this, code)
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

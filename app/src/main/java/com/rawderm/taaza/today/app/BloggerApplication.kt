package com.rawderm.taaza.today.app


import android.app.Application
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.di.initKoin
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext


class BloggerApplication : Application() {
    companion object {
        private val _isWorking = MutableStateFlow(true)   // always true until we know better
        val isWorking: StateFlow<Boolean> get() = _isWorking.asStateFlow()

        private val _mustUpdateVersion = MutableStateFlow(0)   // NEW
        val mustUpdateVersion: StateFlow<Int> get() = _mustUpdateVersion.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()

        initKoin { androidContext(this@BloggerApplication) }
        FirebaseApp.initializeApp(this)

        setupAnalytics()
        setupCrashlytics()
        keepTryingRemoteConfig()
        Lingver.init(this)
        val store = LanguageDataStore(this)
        val language = runBlocking(Dispatchers.IO) { store.getLanguage() }
        Log.d("LANG-START", "DataStore returned: $language")   // ← add this

        Lingver.getInstance().setLocale(this, language)

        /* 4. optional – delete the old conflicting file once */
        deleteSharedPreferences("lingver")

        /* WebView hack  for localisation*/
        try {
            WebView(this).destroy()
        } catch (_: Throwable) {
        }
        Lingver.getInstance().setLocale(this, language)


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
        config.setDefaultsAsync(
            mapOf(
                "isWorking" to true,
                "must_update_version" to 0   // NEW
            )
        )

        // 1. fastest cached value
        config.activate()

        // 2. listen for *server* updates (no extra fetch calls)
        config.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(update: ConfigUpdate) {
                if ("isWorking" in update.updatedKeys || "must_update_version" in update.updatedKeys) {
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

        val muv = Firebase.remoteConfig.getLong("must_update_version").toInt() // NEW
        _mustUpdateVersion.value = muv
        Log.d("RC", "activated must_update_version=$muv")
    }
}
package com.rawderm.taaza.today.bloger.data

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference

class LanguageManager(
    private val languageDataStore: LanguageDataStore,
    private val context: Context
) {
    // Fix: Use proper StateFlow creation
    private val _currentLanguage = AtomicReference("en")
    private val _languageFlow = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _languageFlow.asStateFlow()

    // Mutex for synchronization
    private val languageMutex = Mutex()

    suspend fun initialize() {
        try {
            val savedLanguage = languageDataStore.getLanguage()
            _currentLanguage.set(savedLanguage)
            _languageFlow.value = savedLanguage
            Log.d("LanguageManager", "Initialized with language: $savedLanguage")
        } catch (e: Exception) {
            Log.e("LanguageManager", "Failed to initialize language", e)
            _currentLanguage.set("en")
            _languageFlow.value = "en"
        }
    }

    // Thread-safe language getter
    fun getLanguage(): String = _currentLanguage.get()

    private val _restartPending = MutableStateFlow(false)
    val restartPending: StateFlow<Boolean> = _restartPending.asStateFlow()
suspend fun setLanguage(language: String) {
    languageMutex.withLock {
        try {
            Log.d("LANG", "LanguageManager.setLanguage() called: $language")

            /* 1.  save to disk and wait until fsync returns */
            languageDataStore.saveLanguage(language)
            Log.d("LANG", "DataStore write confirmed for: $language")

            /* 2.  now update in-memory state */
            _currentLanguage.set(language)
            _languageFlow.value = language

            /* 3.  tell Lingver */
            Lingver.getInstance().setLocale(context, language)
            Log.d("LanguageManager", "Language set to: $language")
        } catch (e: Exception) {
            Log.e("LanguageManager", "Failed to set language: $language", e)
            throw e
        }
    }
}
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  fun setLanguageAndRestart(language: String, context: Context) {
    // 1. INSTANT feedback on MAIN thread
    _restartPending.value = true
    Lingver.getInstance().setLocale(context, language) // UI reflects change now

    // 2. real write on IO thread
    appScope.launch {
        Log.d("LANG", "App-scope starting write for: $language")
        languageDataStore.saveLanguage(language)   // suspend, but no .first()
        Log.d("LANG", "App-scope write finished, restarting")
        _restartPending.value = false
        restartApp(context)
    }
}
    fun restartApp(context: Context) {
        val activity = context as Activity
        val intent = Intent(activity, activity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        val options = ActivityOptions.makeCustomAnimation(activity, 0, 0)
        activity.finish()
        activity.startActivity(intent, options.toBundle())

    }
}
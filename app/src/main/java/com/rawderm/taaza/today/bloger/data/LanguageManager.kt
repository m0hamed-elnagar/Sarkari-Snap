package com.rawderm.taaza.today.bloger.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.rawderm.taaza.today.app.DeepLink
import com.rawderm.taaza.today.core.notifications.data.TopicDataStoreManager
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference

class LanguageManager(
    private val languageDataStore: LanguageDataStore,
    private val context: Context,
    private val topicManger: TopicDataStoreManager
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
    fun getLanguage(): String {
        val lang = _currentLanguage.get()
        Log.d("LanguageManager", "getLanguage returning: $lang")
        return lang
    }

    private val _restartPending = MutableStateFlow(false)
    val restartPending: StateFlow<Boolean> = _restartPending.asStateFlow()
    suspend fun setLanguage(language: String, activity: Activity? = null) {
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
                topicManger.switchToLocale(language)
                Log.d("LanguageManager", "Language set to: $language")
                // Set restart pending flag instead of directly recreating
                _restartPending.value = true
                activity?.recreate()
            } catch (e: Exception) {
                Log.e("LanguageManager", "Failed to set language: $language", e)
                throw e
            }
        }
    }

    fun recreateActivity(activity: Activity?) {
        if (_restartPending.value) {
            _restartPending.value = false
            activity?.recreate()
        }
    }
}

object PendingDeepLinkStorage {
    private const val PREFS = "deep_link_prefs"
    private const val KEY_TYPE = "type"   // "post" | "short"
    private const val KEY_DATA = "data"
    private const val KEY_LANG = "lang"

    fun save(ctx: Context, type: String, data: String, lang: String) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TYPE, type)
            .putString(KEY_DATA, data)
            .putString(KEY_LANG, lang)
            .apply()

    fun consume(ctx: Context): Triple<String, String, String>? {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val type = p.getString(KEY_TYPE, null) ?: return null
        val id = p.getString(KEY_DATA, null) ?: return null
        val lang = p.getString(KEY_LANG, null) ?: return null
        p.edit().clear().apply()
        return Triple(type, id, lang)
    }

    /* ---------- read-only ---------- */
    fun get(ctx: Context): DeepLink? =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .let { prefs ->
                val type = prefs.getString(KEY_TYPE, null).orEmpty()
                val id = prefs.getString(KEY_DATA, null).orEmpty()
                val lang = prefs.getString(KEY_LANG, null).orEmpty()

                DeepLink(type, id, lang).takeIf { it.isValid }
            }

    /* ---------- clear ---------- */
    fun clear(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
}

object DeepLinkHandler {
    var consumed = false
}

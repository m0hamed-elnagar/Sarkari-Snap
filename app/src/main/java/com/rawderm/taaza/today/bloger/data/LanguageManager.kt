package com.rawderm.taaza.today.bloger.data

import android.content.Context
import android.util.Log
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference

class LanguageManager(
    private val languageDataStore: LanguageDataStore,
    private val context: Context
) {
    // Fix: Use proper StateFlow creation
    private val _currentLanguage = AtomicReference("hi")
    private val _languageFlow = MutableStateFlow("hi")
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

    suspend fun setLanguage(language: String) {
        languageMutex.withLock {
            try {
                // Update both atomic reference and flow atomically
                _currentLanguage.set(language)
                _languageFlow.value = language
                languageDataStore.saveLanguage(language)

                // Force update Lingver as well
                Lingver.getInstance().setLocale(context, language)
                Log.d("LanguageManager", "Language set to: $language")
            } catch (e: Exception) {
                Log.e("LanguageManager", "Failed to set language: $language", e)
                throw e
            }
        }
    }
}
package com.rawderm.taaza.today.bloger.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs")

class LanguageDataStore(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    // Expose language as Flow - this will emit updates!
    val languageFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "hi" // Default to Hindi
        }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
        context.dataStore.data.first()
        Log.d("LANG-WRITE", "Saved $language and flushed")
    }

    suspend fun getLanguage(): String {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "hi" // Default to Hindi
        }.first()
    }
}
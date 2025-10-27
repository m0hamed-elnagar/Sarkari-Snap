package com.rawderm.taaza.today.bloger.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "language_prefs")

class LanguageDataStore(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        val FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")   // NEW
        val PENDING_LANG_KEY = stringPreferencesKey("pending_deep_link_language")
        val PENDING_INTENT_KEY = stringPreferencesKey("pending_intent_uri")

    }

    suspend fun isFirstLaunch(): Boolean =
        context.dataStore.data.map { it[FIRST_LAUNCH_KEY] ?: true }.first()

    suspend fun markFirstLaunchDone() {
        context.dataStore.edit { it[FIRST_LAUNCH_KEY] = false }
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
            val lang = preferences[LANGUAGE_KEY] ?: "hi" // Default to Hindi
            Log.d("LanguageDataStore", "getLanguage returning: $lang")
            lang
        }.first()
    }

  suspend  fun getLanguageSync(): String = runCatching {
        context.dataStore.data
            .map { it[LANGUAGE_KEY] ?: "hi" }  // same default you already use
            .first()                           // blocks until first value arrives
    }.getOrElse { "hi" }

    // New methods for deep link language handling
    suspend fun savePendingIntent(lang: String, intentUri: String) = context.dataStore.edit {
        Log.d("LanguageDataStore", "Saving pending intent language: $lang, uri: $intentUri")
        it[PENDING_LANG_KEY] = lang
        it[PENDING_INTENT_KEY] = intentUri
    }

    suspend fun getPendingIntent(): String? =
        context.dataStore.data.map {
            val intent = it[PENDING_INTENT_KEY]
            Log.d("LanguageDataStore", "getPendingIntent returning: $intent")
            intent
        }.firstOrNull()

    suspend fun clearPendingIntent() = context.dataStore.edit {
        Log.d("LanguageDataStore", "Clearing pending intent")
        it.remove(PENDING_LANG_KEY)
        it.remove(PENDING_INTENT_KEY)
    }


}
package com.rawderm.taaza.today.bloger.data

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class LanguageDataStore(
    private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private val LANG_KEY = stringPreferencesKey("selected_language")

    val language: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANG_KEY] ?: "hi" // Default to Hindi
        }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANG_KEY] = lang
        }
        Lingver.getInstance().setLocale(context, lang)
        (context as? Activity)?.recreate()
    }
//       init {
//        // Auto-apply locale changes
//        language
//            .onEach { locale ->
//                Lingver.getInstance().setLocale(context, locale)
//                // Removed automatic activity recreation to avoid conflicts with manual language switching
//                // (context as? Activity)?.recreate()
//            }
//            .launchIn(CoroutineScope(Dispatchers.Main + SupervisorJob()))
//    }
}
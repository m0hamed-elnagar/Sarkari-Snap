package com.rawderm.taaza.today.bloger.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "settings")

object LangDataStore {
    private val LANG_KEY = stringPreferencesKey("selected_language")

    suspend fun saveLanguage(context: Context, lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANG_KEY] = lang
        }
    }

    fun getLanguage(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[LANG_KEY] ?: "en"
        }
    }
}
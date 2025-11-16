package com.rawderm.taaza.today.core.notifications.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.FrequencyMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/* --------------------- extension ------------------------- */
private val Context.topicDataStore by preferencesDataStore("fcm_topic_prefs")

/* --------------------- manager --------------------------- */
class TopicDataStoreManager(
    private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    /* ----------------------------------------------------------
     *  FIRST-TIME DIALOG FLAG
     * ---------------------------------------------------------- */
    suspend fun markTopicDialogAlreadyShown() =
        context.topicDataStore.edit { it[TOPIC_DIALOG_SHOWN_KEY] = true }

    fun hasTopicDialogAlreadyShown(): Flow<Boolean> =
        context.topicDataStore.data
            .map { prefs -> prefs[TOPIC_DIALOG_SHOWN_KEY] ?: false }

    suspend fun applyTopicPolicy(newIds: List<Int>, newMode: FrequencyMode,locale: String) {

    /* ---------- 1.  previous snapshot  ---------- */
        val oldTopics =readFcmSnapshot()

        // 1. persist the tiny data
        context.topicDataStore.edit { prefs ->
            prefs[MODE_KEY] = newMode.name
            prefs[IDS_KEY] = newIds.sorted().joinToString(",")
            prefs[LAST_LOCALE_KEY] = locale
        }

        // 2. build current FCM topic set for current locale
    val newTopics = buildTopicStrings(newIds, newMode, locale)

        // 3. sync with FCM
    synchronizeWithFcm(desiredTopics = newTopics, previousTopics = oldTopics)
    }

    private suspend fun readFcmSnapshot(): Set<String> {
        val prefs = context.topicDataStore.data.first()
        val locale = prefs[LAST_LOCALE_KEY]?:"hi"
        val mode = prefs[MODE_KEY]?.let { FrequencyMode.valueOf(it) } ?: FrequencyMode.STANDARD
        val ids = prefs[IDS_KEY]?.split(",")?.mapNotNull { it.toIntOrNull() }.orEmpty()
        return buildTopicStrings(ids, mode, locale)
    }

    private fun buildTopicStrings(
        ids: List<Int>,
        mode: FrequencyMode,
        locale: String
    ): Set<String> = buildSet {
        ids.distinct().forEachIndexed { index, id ->
            val def = TOPICS_LIST.first { it.id == id }
            val label = (if (locale == "hi") def.en+"_hi" else def.en)

            when (mode) {
                FrequencyMode.BREAKING -> {
                    add("${label}-high")
                    add("${label}-normal")
                }

                FrequencyMode.STANDARD -> {
                    add("${label}-high")
                    if (index < ids.size / 2)
                        add("${label}-normal")
                }

                FrequencyMode.Custom -> {
                    add("${label}-high")

                }
            }
        }
    }

    suspend fun switchToLocale(newLocale: String) {
        val prefs = context.topicDataStore.data.first()
        val ids   = prefs[IDS_KEY]?.split(",").orEmpty()
            .mapNotNull { it.toIntOrNull() }
        val mode  = prefs[MODE_KEY]?.let { FrequencyMode.valueOf(it) } ?: return
        applyTopicPolicy(ids, mode,newLocale)   // will use the new locale
    }

data class TopicSnapshot(
    val ids: List<Int>,
    val mode: FrequencyMode
)


    suspend fun getCurrentSnapshot(): TopicSnapshot {
        val prefs = context.topicDataStore.data.first()   // one-shot
        val mode  = prefs[MODE_KEY]?.let { FrequencyMode.valueOf(it) } ?: FrequencyMode.STANDARD
        val ids   = prefs[IDS_KEY]?.split(",").orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .distinct()
            .sorted()
        return TopicSnapshot(ids, mode)
    }

    private suspend fun synchronizeWithFcm(
        desiredTopics: Set<String>,
        previousTopics: Set<String>
    ) {
        (desiredTopics - previousTopics).forEach { subscribeToFcmTopic(it) }
        (previousTopics - desiredTopics).forEach { unsubscribeFromFcmTopic(it) }
    }

    private fun subscribeToFcmTopic(topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
        Log.d("FCM", "Subscribed to $topic")
    }

    private fun unsubscribeFromFcmTopic(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic)
        Log.d("FCM", "Unsubscribed from $topic")
    }

    companion object {
        private val LAST_LOCALE_KEY = stringPreferencesKey("last_fcm_locale")
        private val MODE_KEY = stringPreferencesKey("topic_mode")
        private val IDS_KEY = stringPreferencesKey("topic_ids")
        private val TOPIC_DIALOG_SHOWN_KEY = booleanPreferencesKey("topics_first_ask_done")
    }
}

/* ----------------------------------------------------------
 *  NORMAL -> HIGH + NORMAL , HIGH -> HIGH , NONE -> drop
 * ---------------------------------------------------------- */
private fun expandImportance(map: Map<String, Importance>): Map<String, Importance> =
    buildMap {
        map.forEach { (topic, imp) ->
            when (imp) {
                Importance.NORMAL -> {
                    put(topic, Importance.HIGH)
                    put(topic, Importance.NORMAL)
                }

                Importance.HIGH -> put(topic, Importance.HIGH)
                Importance.NONE -> { /* ignore */
                }
            }
        }
    }

fun TopicDef.fcmTopic(imp: Importance, locale: String): String {
    val label = if (locale == "hi") hi else en
    return "${label.lowercase().replace(" ", "_")}-${imp.name.lowercase()}"
}

/* --------------------- model ----------------------------- */
@Serializable
enum class Importance { HIGH, NORMAL, NONE }

data class TopicItem(
    val topicName: String,
    val importance: Importance
)
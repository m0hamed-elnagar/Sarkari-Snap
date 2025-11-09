package com.rawderm.taaza.today.core.notifications.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/* --------------------- extension ------------------------- */
private val Context.topicDataStore by preferencesDataStore("fcm_topic_prefs")

/* --------------------- manager --------------------------- */
class TopicDataStoreManager(
    private val context: Context
) {

    private val json = Json { ignoreUnknownKeys = true }

    /* ----------------------------------------------------------
     *  FIRST-TIME DIALOG FLAG
     * ---------------------------------------------------------- */
    suspend fun markTopicDialogAlreadyShown() =
        context.topicDataStore.edit { it[TOPIC_DIALOG_SHOWN_KEY] = true }

    fun hasTopicDialogAlreadyShown(): Flow<Boolean> =
        context.topicDataStore.data
            .map { prefs -> prefs[TOPIC_DIALOG_SHOWN_KEY] ?: false }

    /* ----------------------------------------------------------
     *  BULK WRITE - completely replace local & FCM list
     * ---------------------------------------------------------- */
    suspend fun replaceAllTopics(topicMap: Map<String, Importance>) {
        val previous = readSnapshot()
        context.topicDataStore.edit { prefs ->
            prefs[TOPIC_MAP_KEY] = json.encodeToString(topicMap)
        }
        synchronizeWithFcm(desiredTopics = topicMap.keys, previousTopics = previous.keys)
    }

    /* ----------------------------------------------------------
     *  SINGLE WRITE - add or change importance
     * ---------------------------------------------------------- */
    suspend fun addOrUpdateTopic(topic: String, importance: Importance) {
        context.topicDataStore.edit { prefs ->
            val current = decodeMap(prefs[TOPIC_MAP_KEY]).toMutableMap()
            val isNew = current[topic] == null
            current[topic] = importance
            prefs[TOPIC_MAP_KEY] = json.encodeToString(current)

            if (isNew) subscribeToFcmTopic(topic)
        }
    }

    /* ----------------------------------------------------------
     *  SINGLE DELETE - local + FCM
     * ---------------------------------------------------------- */
    suspend fun removeTopic(topic: String) {
        context.topicDataStore.edit { prefs ->
            val current = decodeMap(prefs[TOPIC_MAP_KEY]).toMutableMap()
            current.remove(topic)
            prefs[TOPIC_MAP_KEY] = json.encodeToString(current)
        }
        unsubscribeFromFcmTopic(topic)
    }


    /* ----------------------------------------------------------
     *  LANGUAGE SWITCH - same IDs, new locale label
     * ---------------------------------------------------------- */
    suspend fun switchToLocale(newLocale: String) { // "en" | "hi"
        val oldMap = readSnapshot()                 // old label_importance
        val newMap = oldMap.mapNotNull { (oldTopic, importance) ->
            val oldLabel = oldTopic.substringBefore("-")
            val definition = TOPICS_LIST.firstOrNull {
                it.en.equals(oldLabel, true) || it.hi.equals(oldLabel, true)
            } ?: return@mapNotNull null
            definition.fcmTopic(importance, newLocale) to importance
        }.toMap()
        replaceAllTopics(newMap)
    }




    /** Observe list everywhere */
    fun observeTopics(): Flow<List<TopicItem>> =
        context.topicDataStore.data
            .map { prefs -> decodeMap(prefs[TOPIC_MAP_KEY]) }
            .map { map -> map.map { (topic, importance) -> TopicItem(topic, importance) } }

    /* ==========================================================
     *  PRIVATE  -  helpers
     * ========================================================== */
    private suspend fun readSnapshot(): Map<String, Importance> =
        decodeMap(context.topicDataStore.data.first()[TOPIC_MAP_KEY])

    private fun decodeMap(raw: String?): Map<String, Importance> =
        raw?.let { json.decodeFromString<Map<String, Importance>>(it) } ?: emptyMap()

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
        private val TOPIC_MAP_KEY = stringPreferencesKey("fcm_topic_importance")
        private val TOPIC_DIALOG_SHOWN_KEY = booleanPreferencesKey("topics_first_ask_done")
    }
}
fun TopicDef.fcmTopic(imp: Importance, locale: String): String {
    val label = if (locale == "hi") hi else en
    return "${label.lowercase().replace(" ", "")}-${imp.name.lowercase()}"
}
/* --------------------- model ----------------------------- */
@Serializable
enum class Importance { HIGH, NORMAL, NONE }

data class TopicItem(
    val topicName: String, // "sports_high"
    val importance: Importance
)
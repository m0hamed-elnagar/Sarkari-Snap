package com.rawderm.taaza.today.core.notifications.data

import kotlinx.serialization.Serializable

@Serializable
data class TopicDef(
    val id: Int,                 // stable across locales
    val en: String,
    val hi: String
)

val TOPICS_LIST = listOf(
    TopicDef(0, "all", "सभी"),
    TopicDef(1, "politics", "राजनीति"),
    TopicDef(2, "crime", "अपराध"),
    TopicDef(3, "entertainment", "मनोरंजन"),
    TopicDef(4, "sports", "खेल"),
    TopicDef(5, "business", "व्यापार"),
    TopicDef(6, "tech", "टेक्नोलोजी"),
    TopicDef(7, "science", "विज्ञान"),
    TopicDef(8, "automobile", "ऑटोमोबाइल"),
    TopicDef(9, "education", "शिक्षा"),
    TopicDef(10, "job_News", "रोजगार_समाचार"),
    TopicDef(11, "yojana", "सरकारी_योजनाएं"),
    TopicDef(12, "finance", "फाइनेंस"),
    TopicDef(13, "health", "स्वास्थ्य"),
    TopicDef(14, "world", "विश्व_समाचार"),
    TopicDef(15, "viral", "वायरल_खबरें"),
    TopicDef(16, "shorts", "शॉर्ट्स"),
    TopicDef(17, "quiks", "क्विक्स"),
)
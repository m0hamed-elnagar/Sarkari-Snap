package com.rawderm.taaza.today.core.notifications.data

import kotlinx.serialization.Serializable

@Serializable
data class TopicDef(
    val id: Int,                 // stable across locales
    val en: String,
    val hi: String
)

val TOPICS_LIST = listOf(
    TopicDef(0, "All", "सभी"),
    TopicDef(1, "Politics", "राजनीति"),
    TopicDef(2, "Crime", "अपराध"),
    TopicDef(3, "Entertainment", "मनोरंजन"),
    TopicDef(4, "Sports", "खेल"),
    TopicDef(5, "Business", "व्यापार"),
    TopicDef(6, "Tech", "टेक्नोलॉजी"),
    TopicDef(7, "Science", "विज्ञान"),
    TopicDef(8, "Automobile", "ऑटोमोबाइल"),
    TopicDef(9, "Education", "शिक्षा"),
    TopicDef(10, "Job News", "रोजगार समाचार"),
    TopicDef(11, "Yojana", "सरकारी योजनाएं"),
    TopicDef(12, "Finance", "फाइनेंस / वित्त"),
    TopicDef(13, "Health", "स्वास्थ्य"),
    TopicDef(14, "World", "विश्व समाचार"),
    TopicDef(15, "Viral", "वायरल खबरें"),
    TopicDef(16, "Shorts", "शॉर्ट्स"),
    TopicDef(17, "Quiks", "क्विक्स"),
)
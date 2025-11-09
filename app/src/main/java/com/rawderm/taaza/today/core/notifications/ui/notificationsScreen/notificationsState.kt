package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen


data class NotificationCategory(
    val id : Int,
    val category: String,
    val level: FrequencyMode= FrequencyMode.BREAKING,
)
enum class FrequencyMode { BREAKING, STANDARD, Custom }


enum class SelectionMode { All, Important,Custom }

data class NotificationsScreenState(
    val selectionMode: SelectionMode = SelectionMode.All,
    val categories: List<NotificationCategory> = emptyList(),
    val frequencyMode: FrequencyMode = FrequencyMode.BREAKING,
    val selectedCategories: Set<Int> =(1..15).toSet(),
    val isLoading: Boolean = false,
)

package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen

sealed interface NotificationsActions {
    data object OnBackClick : NotificationsActions
    data class OnTopicClick(val topicId: Int) : NotificationsActions
    data class OnModeClick(val selectionMode: SelectionMode) : NotificationsActions
    data class OnCategoryFrequencyClick(val frequencyMode: FrequencyMode) : NotificationsActions
    data class OnTopicLevelChange(val topicId: Int, val level: FrequencyMode) : NotificationsActions
    data object OnSaveTopicsClick : NotificationsActions
}
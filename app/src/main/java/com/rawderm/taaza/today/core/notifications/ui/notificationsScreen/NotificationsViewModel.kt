package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.core.notifications.data.TOPICS_LIST
import com.rawderm.taaza.today.core.notifications.data.TopicDataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsViewModel(
    private val manager: TopicDataStoreManager,
    private val languageManager: LanguageManager

) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsScreenState())
    val state: StateFlow<NotificationsScreenState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        _state.value
    )

    init {
        viewModelScope.launch {
            /* 1. subscribe test topic (keep old behaviour) */
            Firebase.messaging.subscribeToTopic("test").await()
        }

        viewModelScope.launch {
            languageManager.currentLanguage.collect {
                refreshSnapshot()
            }
        }
    }
    private suspend fun refreshSnapshot() {
        val (ids, mode) = manager.getCurrentSnapshot()
        val locale = languageManager.getLanguage()

        val categories = TOPICS_LIST.map { def ->
            val category=if (locale == "hi") def.hi else def.en
            NotificationCategory(
                id = def.id,
                category = category.replace("_", " "),
                level = when (mode) {
                    FrequencyMode.BREAKING -> FrequencyMode.BREAKING
                    FrequencyMode.STANDARD -> {
                        val index = ids.indexOf(def.id)
                        if (index >= 0 && index < ids.size / 2) FrequencyMode.BREAKING
                        else FrequencyMode.STANDARD
                    }
                    FrequencyMode.Custom -> FrequencyMode.Custom
                }
            )
        }
        val selected = if (ids.isEmpty()) {
            TOPICS_LIST.map { it.id }.toSet()
        } else {
            ids.toSet()
        }
        _state.update {
            it.copy(
                categories = categories,
                selectedCategories = selected,
            frequencyMode = mode,
            selectionMode = if (ids.isEmpty()) SelectionMode.All else SelectionMode.Custom,
            isLoading = false
        )
    }}


    /* ------------------------------------------------------------------
     * User actions
     * ------------------------------------------------------------------ */
    fun onAction(action: NotificationsActions) {
        when (action) {
            is NotificationsActions.OnModeClick ->
                setSelectionMode(action.selectionMode)

            is NotificationsActions.OnCategoryFrequencyClick ->
                setFrequency(action.frequencyMode)

            is NotificationsActions.OnTopicClick ->
                toggleTopic(action.topicId)

            is NotificationsActions.OnTopicLevelChange ->
                changeTopicLevel(action.topicId, action.level)

            is NotificationsActions.OnSaveTopicsClick ->
                saveCurrentChoices()

            else -> {}
        }
    }

    fun setFrequency(frequencyMode: FrequencyMode) {
        _state.update { it.copy(frequencyMode = frequencyMode) }
    }

    fun setSelectionMode(mode: SelectionMode) {
        val newSelection = when (mode) {
            SelectionMode.All -> TOPICS_LIST.map { it.id }.toSet()
            SelectionMode.Important -> TOPICS_LIST.take(5).map { it.id }.toSet()
            SelectionMode.Custom -> state.value.selectedCategories
        }
        _state.update {
            it.copy(
                selectedCategories = newSelection,
                selectionMode = mode
            )
        }
    }

    fun toggleTopic(topicId: Int) {
        if (topicId == 0 ){
            setSelectionMode(SelectionMode.All)
        }else{
        val curr = state.value.selectedCategories
        val newSet = if (topicId in curr) curr - topicId -0 else curr + topicId -0
        _state.update {
            it.copy(
                selectedCategories = newSet,
                selectionMode = SelectionMode.Custom
            )
        }}
    }

    fun changeTopicLevel(id: Int, level: FrequencyMode) {
        _state.update { st ->
            st.copy(
                categories = st.categories.map { cat ->
                    if (cat.id == id) cat.copy(level = level) else cat
                },
                frequencyMode = FrequencyMode.Custom,
            )
        }
    }

    fun saveCurrentChoices() {
        viewModelScope.launch {
            val locale = languageManager.getLanguage()
             val newIds = state.value.selectedCategories.toList()
            val newMode = state.value.frequencyMode
            manager.applyTopicPolicy(newIds = newIds, newMode = newMode ,locale)
        }
    }


}
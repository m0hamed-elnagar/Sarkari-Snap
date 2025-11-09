package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.core.notifications.data.Importance
import com.rawderm.taaza.today.core.notifications.data.ImportanceMapper
import com.rawderm.taaza.today.core.notifications.data.TOPICS_LIST
import com.rawderm.taaza.today.core.notifications.data.TopicDataStoreManager
import com.rawderm.taaza.today.core.notifications.data.fcmTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
            languageManager.currentLanguage.collect { locale ->
                val stored = manager.observeTopics().first()
                val idToImp = stored.mapNotNull { item ->
                    val label = item.topicName.substringBefore("-")
                    val id = TOPICS_LIST.firstOrNull {
                        it.en.equals(label, true) || it.hi.equals(label, true)
                    }?.id

                    id?.let { it to item.importance }
                }.toMap()
                val selected = if (idToImp.isEmpty()) {
                    TOPICS_LIST.map { it.id }.toSet()
                } else {
                    idToImp.keys.toSet()
                }
                _state.update {
                    it.copy(
                        categories = buildCategoryList(locale, idToImp),
                        selectedCategories = selected,
                        selectionMode = if (idToImp.keys.isEmpty()) SelectionMode.All
                        else SelectionMode.Custom,
                        isLoading = false
                    )
                }
            }
        }
    }


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
        when (frequencyMode) {
            FrequencyMode.BREAKING -> {
                _state.update { current ->
                    current.copy(
                        frequencyMode = frequencyMode,
                        categories = current.categories.map { category ->
                            if (category.id in current.selectedCategories) {
                                category.copy(level = frequencyMode)
                            } else category
                        }
                    )
                }
            }
            FrequencyMode.STANDARD -> {
                _state.update { current ->
                    val selected = current.categories.filter { it.id in current.selectedCategories }
                    val halfSize = selected.size / 2

                    current.copy(
                        frequencyMode = frequencyMode,
                        categories = current.categories.mapIndexed { index, category ->
                            if (category.id in current.selectedCategories) {
                                val indexInSelected = selected.indexOf(category)
                                val newLevel = if (indexInSelected < halfSize) {
                                    FrequencyMode.BREAKING
                                } else {
                                    FrequencyMode.STANDARD
                                }
                                category.copy(level = newLevel)
                            } else category
                        }
                    )
                }
            }


            FrequencyMode.Custom -> {
                _state.update { it.copy(frequencyMode = FrequencyMode.Custom) }
            }
        }

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
            val map = state.value.categories
                .filter { it.id in state.value.selectedCategories }
                .associate { cat ->
                    val imp = ImportanceMapper.toImportance(cat.level)
                    /* rebuild correct FCM topic string */
                    TOPICS_LIST.first { it.id == cat.id }
                        .fcmTopic(imp, locale) to imp
                }
            manager.replaceAllTopics(map)
        }
    }

    /* ------------------------------------------------------------------
     * Private helpers
     * ------------------------------------------------------------------ */
    private fun buildCategoryList(
        locale: String,
        idToImp: Map<Int, Importance>
    ): List<NotificationCategory> =
    TOPICS_LIST.map { def ->
        NotificationCategory(
            id = def.id,
            category = if (locale == "hi") def.hi else def.en,
            level = idToImp[def.id]?.let(ImportanceMapper::toFrequencyMode)
                ?: FrequencyMode.BREAKING
        )
    }
}
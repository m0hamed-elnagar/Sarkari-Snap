package com.rawderm.taaza.today.bloger.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.core.domain.onError
import com.rawderm.taaza.today.core.domain.onSuccess
import com.rawderm.taaza.today.core.ui.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repo: PostsRepo,
    private val languageManager: LanguageManager // Inject LanguageManager
) : ViewModel() {

    // Use LanguageManager's current language flow
    private val lang: Flow<String> = languageManager.currentLanguage

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state
        .onStart {
            fetchLabels()
            observeFavoriteStatus()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    // Paging 3: Expose paged posts for the current label
    private val _currentLabel = MutableStateFlow(_state.value.selectedLabel)
    val pagedPosts: Flow<PagingData<Post>> = combine(
        _currentLabel,
        lang
    ) { label, language ->
        Log.d("HomeViewModel", "Creating posts flow with label: $label, language: $language")
        label to language
    }.flatMapLatest { (label, language) ->
        val finalLabel = if (label == "All") null else label
        repo.getPagedPosts(label = finalLabel)
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
    
    val trendingPosts: Flow<PagingData<Post>> = lang.flatMapLatest { language ->
        Log.d("HomeViewModel", "Creating trending posts flow with language: $language")
        repo.getPagedPosts("Trending")
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
    
    val pages: Flow<PagingData<Page>> = lang.flatMapLatest { language ->
        Log.d("HomeViewModel", "Creating pages flow with language: $language")
        repo.getPages()
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
    
    private var observeFavoritesJob: Job? = null

    fun onAction(action: HomeActions) {
        when (action) {
            is HomeActions.OnTabSelected ->
                _state.update { it.copy(selectedTabIndex = action.index) }


            is HomeActions.OnLabelSelected -> {
                _state.update {
                    it.copy(
                        selectedLabel = action.label,
                        isLoading = true
                    )
                }
                _currentLabel.value = action.label
            }
            is HomeActions.OnRefresh -> refreshAll()
            is HomeActions.OnLoading -> _state.update { it.copy(isLoading = true) }
            is HomeActions.ChangeLanguage -> changeLanguage(action.language)
            // Remove OnNextPage logic (Paging 3 handles this)
            else -> Unit
        }
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Changing language to: $language")
                languageManager.setLanguage(language)
                Log.d("HomeViewModel", "Language changed successfully to: $language")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to change language", e)
            }
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            fetchLabels()
            _currentLabel.value = _currentLabel.value
        }
    }

    private fun observeFavoriteStatus() {
        observeFavoritesJob?.cancel()
        observeFavoritesJob = viewModelScope.launch {
            repo.getFavoritePosts()
                .onEach { fav -> _state.update { it.copy(favoritePosts = fav) } }
                .launchIn(viewModelScope)
            repo.getFavoriteShorts()
                .onEach { fav -> _state.update { it.copy(favoriteShorts = fav) } }
                .launchIn(viewModelScope)
        }
    }

    private fun fetchLabels() = viewModelScope.launch {
        repo.getLabels()
            .onSuccess { labels ->
                _state.update { it.copy(labels = labels.filter { label -> label != "Trending" }, errorMessage = null) }
            }
            .onError { error ->
                _state.update { it.copy(errorMessage = error.toUiText()) }
            }
    }
}
package com.rawderm.taaza.today.bloger.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repo: PostsRepo,
    private val languageManager: LanguageManager // Inject LanguageManager
) : ViewModel() {

    // Use LanguageManager's current language flow
    private val lang= languageManager.currentLanguage
        .onEach {
            fetchLabels()

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            ""
        )

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

    val quickPosts: Flow<PagingData<Post>> = lang.flatMapLatest { language ->
        Log.d("HomeViewModel", "Creating trending posts flow with language: $language")
        repo.getPagedPosts("Quiks")
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
    val pagedUiModels: Flow<PagingData<PostUiItem>> = pagedPosts.map { pagingData ->
        var adCounter = AtomicInteger(1)          // 1-based position in the list
        pagingData
            .map { post ->
                PostUiItem.post(post)
            }
            .insertSeparators { before, after ->
                // Insert a banner every 3 posts
                if (before == null) return@insertSeparators null // start of list
                if (adCounter.get() % 5== 0) {
                    Log.d("adcounter2", adCounter.get().toString())
                    adCounter.incrementAndGet()
                    PostUiItem.ad()
                } else {
                    adCounter.incrementAndGet()
                    null
                }
            }
    }

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
    private var observeLabelsJob: Job? = null

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
            // Remove OnNextPage logic (Paging 3 handles this)
            else -> Unit
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
                _state.update {
                    it.copy(
                        labels = labels,
                        errorMessage = null,
                        selectedLabel = labels.first()
                    )
                }
            }
            .onError { error ->
                _state.update { it.copy(errorMessage = error.toUiText()) }
            }
    }
}
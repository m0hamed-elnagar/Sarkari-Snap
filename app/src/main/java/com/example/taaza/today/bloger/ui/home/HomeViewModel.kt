package com.example.taaza.today.bloger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repo: PostsRepo
) : ViewModel() {

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
    val pagedPosts: Flow<PagingData<Post>> = _currentLabel.flatMapLatest { label ->
        repo.getPagedPosts(if (label == "All") null else label)
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
val trendingPosts: Flow<PagingData<Post>> = repo.getPagedPosts("Trending")
    .cachedIn(viewModelScope)
    .stateIn(
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
            // Remove OnNextPage logic (Paging 3 handles this)
            else -> Unit
        }
    }


    private fun observeFavoriteStatus() {
        observeFavoritesJob?.cancel()
        observeFavoritesJob = viewModelScope.launch {
            repo.getFavoritePosts()
                .onEach { fav -> _state.update { it.copy(favoritePosts = fav) } }
                .launchIn(viewModelScope)
        }
    }

    private fun fetchLabels() = viewModelScope.launch {
        repo.getLabels()
            .onSuccess { labels ->
                _state.update { it.copy(labels = labels.filter { label->label  !="Trending" }, errorMessage = null) }
            }
            .onError { error ->
                _state.update { it.copy(errorMessage = error.toUiText()) }
            }
    }
}
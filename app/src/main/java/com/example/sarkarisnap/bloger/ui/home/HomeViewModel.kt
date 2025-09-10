package com.example.sarkarisnap.bloger.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: PostsRepo
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state
        .onStart {
            fetchLabels()
            observeFavoriteStatus()
            fetchPosts(isRefresh = true)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    private data class PagingInfo(
        var nextPageToken: String? = null,
        var isLastPage: Boolean = false
    )

    private val pageInfoMap = mutableMapOf<String, PagingInfo>()
    private var currentJob: Job? = null

    private fun infoFor(label: String) =
        pageInfoMap.getOrPut(label) { PagingInfo() }

    private var observeFavoritesJob: Job? = null

    fun onAction(action: HomeActions) {
        when (action) {
            is HomeActions.OnTabSelected ->
                _state.update { it.copy(selectedTabIndex = action.index) }

            HomeActions.OnRefresh ->
                fetchPosts(isRefresh = true)

            is HomeActions.OnLabelSelected ->
                viewModelScope.launch {
                   pageInfoMap[action.label] = PagingInfo()
                    _state.update { it.copy(selectedLabel = action.label, posts = emptyList()) }
                    fetchPosts(isRefresh = true)
                }

            HomeActions.OnNextPage -> {
                Log.d("nextpage", "onAction:${infoFor(state.value.selectedLabel).nextPageToken} ")
                fetchPosts(isRefresh = false)
            }
            else -> Unit
        }
    }

    private fun fetchPosts(isRefresh: Boolean) {
        if (currentJob?.isActive == true) return

        val label = state.value.selectedLabel
        val info = infoFor(label)

        if (!isRefresh && info.isLastPage) return          // nothing more

        currentJob = viewModelScope.launch {
            _state.update {
                if (isRefresh) it.copy(isRefreshing = true)
                else it.copy(isLoadingMore = true)
            }

            val token = if (isRefresh) null else info.nextPageToken

            val result =
                if (label == "All") repo.getHomePosts(6, token)
                else repo.getRelatedPosts(6, label, token)

            result.onSuccess { (posts, newToken) ->
                Log.d("PAGING", "label=$label  posts=${posts.size}  newToken=$newToken")

                info.nextPageToken = newToken
                info.isLastPage = newToken == null

            val merged = if (isRefresh) posts
                         else (state.value.posts + posts).distinctBy(Post::id)

                _state.update {
                    it.copy(
                        posts = merged,
                        errorMessage = null,
                        isRefreshing = false,
                        isLoadingMore = false
                    )
                }
            }.onError { err ->
                _state.update {
                    it.copy(
                        errorMessage = err.toUiText(),
                        isRefreshing = false,
                        isLoadingMore = false
                    )
                }
            }
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
                _state.update { it.copy(labels = labels, errorMessage = null) }
            }
            .onError { error ->
                _state.update { it.copy(errorMessage = error.toUiText()) }
            }
    }
}
package com.example.sarkarisnap.bloger.ui.postDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.getOrElse

class PostDetailsViewModel(
    private val postsRepo: PostsRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PostDetailsState())
    val state: StateFlow<PostDetailsState> = _state.onStart {

    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    fun onAction(action: PostDetailsActions) {
        when (action) {
            PostDetailsActions.OnBackClick -> {}
            is PostDetailsActions.OnPostFavoriteClick -> {
                _state.value = _state.value.copy(isFavorite = !_state.value.isFavorite)

            }
            is PostDetailsActions.OnSelectedPostChange -> {
                _state.value = _state.value.copy(post = action.post)
                loadPostDataSequentially(2, action.post.labels)

            }


           else -> {}
        }
    }
    private fun loadPostDataSequentially(limit: Int, labels: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            loadRelatedPosts(limit, labels)
            delay(100)
            loadLatestArticles(limit)
        }
    }
    private fun loadRelatedPosts(limit: Int, labels:List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoadingRelated = true, relatedError = null, relatedFetched = true) }

            postsRepo.getRelatedPosts(limit, labels.firstOrNull().orEmpty())
                .onSuccess { list ->
                    _state.update { currentState ->
                        // FIX: Only update if list actually changed
                        if (currentState.relatedPosts != list) {
                            currentState.copy(
                                relatedPosts = list,
                                isLoadingRelated = false
                            )
                        } else {
                            currentState.copy(isLoadingRelated = false)
                        }
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoadingRelated = false,
                            relatedError = error.toUiText()
                        )
                    }
                }
        }

    }
    private fun loadLatestArticles(limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoadingLatestArticles = true, latestArticlesError = null) }

            postsRepo.getHomePosts(limit)
                .onSuccess { list ->
                    _state.update { currentState ->      if (currentState.latestArticlesPosts != list) {
                        currentState.copy(
                            latestArticlesPosts = list,
                            isLoadingLatestArticles = false,
                            latestArticlesFetched = true
                        )
                    } else {
                        currentState.copy(
                            isLoadingLatestArticles = false,
                            latestArticlesFetched = true
                        )
                    }}
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoadingLatestArticles = false,
                            latestArticlesError = error.toUiText(),
                            latestArticlesFetched = false
                        )
                    }
                }
        }

    }
}
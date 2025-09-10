package com.example.sarkarisnap.bloger.ui.labeled

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.app.Route
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LabeledPostsViewModel(
    private val repo: PostsRepo,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private var cachedPosts = emptyList<Post>()
    val label = savedStateHandle.toRoute<Route.LabeledPosts>().label

    private val _state = MutableStateFlow(LabeledPostsUiState())
    val state: StateFlow<LabeledPostsUiState> = _state
        .onStart {
            _state.update { it.copy(title = label) }
            if (cachedPosts.isEmpty()) loadPosts() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )


    fun onAction(action: LabeledPostsActions){
        when(action){
            LabeledPostsActions.OnRefresh -> fetchPosts(isRefresh = true)
            else -> Unit
        }}
    private fun loadPosts() = fetchPosts(isRefresh = false)

    private fun fetchPosts(isRefresh: Boolean) {
        viewModelScope.launch {


            _state.update { it.copy(flag = true, isRefresh = isRefresh) }
            repo.getRelatedPosts(20, label)
                .onSuccess { pair ->
                    val posts = pair.first
                    cachedPosts = posts
                    _state.update {
                        it.copy(
                            posts        = posts,
                            errorMessage = null
                        ).copy(flag = false, isRefresh = isRefresh)
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.toUiText(),
                               ).copy(flag = false, isRefresh = isRefresh)
                    }}
        }
    }}

private fun LabeledPostsUiState.copy(flag: Boolean, isRefresh: Boolean): LabeledPostsUiState =
    if (isRefresh) copy(isRefreshing = flag)
    else           copy(isLoading   = flag)
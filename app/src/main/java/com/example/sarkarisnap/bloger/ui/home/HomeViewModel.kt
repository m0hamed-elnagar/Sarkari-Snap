package com.example.sarkarisnap.bloger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.onFailure
import kotlin.text.clear

class HomeViewModel(
    private val repo: PostsRepo
) : ViewModel() {

    private var cachedPosts = emptyList<Post>()

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state
        .onStart { if (cachedPosts.isEmpty()) loadPosts() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )


    fun onAction(action: HomeActions){
        when(action){
            is HomeActions.OnTabSelected -> {
                _state.update { it.copy(selectedTabIndex = action.index) } }

            HomeActions.OnRefresh -> fetchPosts(isRefresh = true)
            else -> Unit
        }}
    private fun loadPosts() = fetchPosts(isRefresh = false)

    private fun fetchPosts(isRefresh: Boolean) {
        viewModelScope.launch {


            _state.update { it.copy(flag = true, isRefresh = isRefresh) }
            repo.getHomePosts(20)
                .onSuccess { posts ->
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

private fun HomeUiState.copy(flag: Boolean, isRefresh: Boolean): HomeUiState =
    if (isRefresh) copy(isRefreshing = flag)
    else           copy(isLoading   = flag)
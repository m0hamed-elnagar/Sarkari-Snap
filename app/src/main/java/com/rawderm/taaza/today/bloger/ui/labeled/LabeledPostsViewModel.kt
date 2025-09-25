package com.rawderm.taaza.today.bloger.ui.labeled

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.app.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
    val label = savedStateHandle.toRoute<Route.LabeledPosts>().label

    // Paging 3: Expose paged posts for the label
    val pagedPosts: Flow<PagingData<Post>> = repo.getPagedPosts(label)

    private val _state = MutableStateFlow(LabeledPostsUiState())
    val state: StateFlow<LabeledPostsUiState> = _state
        .onStart {
            _state.update { it.copy(title = label) }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    fun onAction(action: LabeledPostsActions) {
        when (action) {
            LabeledPostsActions.OnRefresh -> triggerRefresh()
            else -> Unit
        }
    }

    // For pull-to-refresh: update isRefreshing in state
    private fun triggerRefresh() {
        _state.update { it.copy(isRefreshing = true) }
        // Simulate refresh completion after a short delay
        viewModelScope.launch {
            delay(800)
            _state.update { it.copy(isRefreshing = false) }
        }
    }
}

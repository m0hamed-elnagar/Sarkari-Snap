package com.example.sarkarisnap.bloger.ui.postDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sarkarisnap.bloger.domain.PostsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

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
            is PostDetailsActions.OnPostFavoriteClick -> {}
            is PostDetailsActions.OnSelectedPostChange -> {
                _state.value = _state.value.copy(post = action.post)
            }

            is PostDetailsActions.OnLinkClicked ->{}
        }
    }
}
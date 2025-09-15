package com.example.taaza.today.bloger.ui.postDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.domain.PostsRepo
import com.example.taaza.today.app.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostDetailsViewModel(
    private val postsRepo: PostsRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val postId = savedStateHandle.toRoute<Route.PostDetails>().postId

    private val _state = MutableStateFlow(PostDetailsState())
    val state: StateFlow<PostDetailsState> = _state.onStart {
        observeFavoriteStatus()
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    // Paging 3: Related posts and latest articles
    private val _relatedLabel = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val relatedPostsPaged: Flow<PagingData<Post>> = _relatedLabel.flatMapLatest { label ->
        postsRepo.getPagedPosts(label)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PagingData.empty()
    )
    val latestArticlesPaged: Flow<PagingData<Post>> = postsRepo.getPagedPosts()

    fun onAction(action: PostDetailsActions) {
        when (action) {
            PostDetailsActions.OnBackClick -> {}
            is PostDetailsActions.OnSelectedPostChange -> {
                _state.value = _state.value.copy(post = action.post)
                // Set label for related posts
                _relatedLabel.value = action.post.labels.firstOrNull()
            }

            is PostDetailsActions.OnPostFavoriteClick -> {
                viewModelScope.launch {
                    if (state.value.isFavorite) {
                        postsRepo.removePostFromFavorites(postId)
                    } else {
                        state.value.post?.let { postsRepo.markPostAsFavorite(it) }
                    }
                }
                _state.value = _state.value.copy(isFavorite = !_state.value.isFavorite)
            }

            else -> {}
        }
    }

    fun observeFavoriteStatus() {
        postsRepo.isPostFavorite(postId)
            .onEach { isFavorite ->
                _state.update {
                    it.copy(
                        isFavorite = isFavorite,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
package com.rawderm.taaza.today.bloger.ui.articleDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rawderm.taaza.today.app.Route
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.core.domain.onError
import com.rawderm.taaza.today.core.domain.onSuccess
import com.rawderm.taaza.today.core.ui.toUiText
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
    savedStateHandle: SavedStateHandle
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
    private val _afterDate = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val relatedPostsPaged: Flow<PagingData<Post>> = _relatedLabel.flatMapLatest { label ->
        postsRepo.getPostsAfterDate(label, _afterDate.value).cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PagingData.empty()
    )
    val latestArticlesPaged: Flow<PagingData<Post>> = postsRepo.getPagedPosts()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PagingData.empty()
        )

    fun onAction(action: PostDetailsActions) {
        when (action) {
            PostDetailsActions.OnBackClick -> {}
            is PostDetailsActions.OnSelectedPostChange -> {
                _state.value = _state.value.copy(post = action.post)
                // Set label for related posts
                _relatedLabel.value = action.post.labels.firstOrNull()
                _afterDate.value = action.post.rowDate
            }

            is PostDetailsActions.OnDeepLinkArrived -> {
                viewModelScope.launch {
                    fetchPostDetails(action.postId)
                }
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

    fun fetchPostDetails(postId: String) {
        if (postId.isBlank()) return
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            postsRepo.getPostById(postId).onSuccess { result ->
                _state.value = _state.value.copy(
                    post = result,
                    isLoading = false
                )
            }
                .onError { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.toUiText()
                    )
                }

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
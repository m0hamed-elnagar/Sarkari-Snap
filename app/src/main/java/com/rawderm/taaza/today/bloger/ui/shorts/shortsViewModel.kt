package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val isPlaying: Boolean = false
)
enum class YouTubePlayerState { UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, CUED }

class ShortsViewModel(
    private val postsRepo: PostsRepo
) : ViewModel() {
    val shorts : Flow<PagingData<Post>> = postsRepo.getPagedShorts().cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PagingData.empty()
        )

    private val _currentPlayingIndex = MutableStateFlow<Int?>(null)
    val currentPlayingIndex: StateFlow<Int?> = _currentPlayingIndex

    fun onAction(action: ShortsActions) {
        when (action) {

            is ShortsActions.OnDeepLinkArrived -> {
                viewModelScope.launch {
//                    fetchPostDetails (action.postId)

                }
            }

            is ShortsActions.OnPostFavoriteClick -> {
                viewModelScope.launch {
//                    if (state.value.isFavorite) {
//                        postsRepo.removePostFromFavorites(postId)
//                    } else {
//                        state.value.post?.let { postsRepo.markPostAsFavorite(it) }
//                    }
//                }
//                _state.value = _state.value.copy(isFavorite = !_state.value.isFavorite)
                }

            }

            ShortsActions.OnBackClick -> {}
            is ShortsActions.OnShareClick -> {}
        }
    }
}
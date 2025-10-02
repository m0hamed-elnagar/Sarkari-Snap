package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
    private val _beforeDate = MutableStateFlow(
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
    val beforeDate: StateFlow<String> = _beforeDate.asStateFlow()
    val shorts2 : Flow<PagingData<Post>> = postsRepo.getPagedShorts().cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PagingData.empty()
        )
    val shorts: Flow<PagingData<Post>> =
        _beforeDate
            .flatMapLatest { date -> postsRepo.getShortsBeforeDate(date) }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = PagingData.empty()
            )

    private val _state = MutableStateFlow(ShortsState())
    val state: StateFlow<ShortsState> = _state


    fun onAction(action: ShortsActions) {
        when (action) {

            is ShortsActions.OnDeepLinkArrived -> {
                action.date?.let { isoDate ->
                    _beforeDate.value = isoDate        // <- triggers shorts re-load
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
else -> {}
        }
    }
    fun fetchShortById(postId: String)  {
        if (postId.isBlank()) return
        _state.value = _state.value.copy(isLoading = true,)
        viewModelScope.launch {
            postsRepo.getShortById(postId).onSuccess { result ->
                _state.value = _state.value.copy(
                    post = result,
                    isLoading = false
                )
            }
                .onError { error ->
                    _state.value = _state.value.copy(
                        errorMessage = error.toUiText(),
                    )
                }

        }
    }

}
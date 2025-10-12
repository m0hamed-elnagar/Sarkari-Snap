package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rawderm.taaza.today.bloger.data.paging.addOneSecond
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.bloger.domain.Short
import com.rawderm.taaza.today.core.domain.onError
import com.rawderm.taaza.today.core.domain.onSuccess
import com.rawderm.taaza.today.core.ui.toUiText
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class ShortsViewModel(
    private val postsRepo: PostsRepo
) : ViewModel() {
    private val _beforeDate = MutableStateFlow(
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
    private val favoriteIds: Flow<Set<String>> =
        postsRepo.observeFavoriteShortIds()
    val shorts: Flow<PagingData<Short>> =
        _beforeDate
            .flatMapLatest { date -> postsRepo.getShortsBeforeDate(date) }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = PagingData.empty()
            )
    val uiShorts: Flow<PagingData<ShortUiItem>> =
        shorts.combine(favoriteIds) { paging, ids ->
            paging.map { short ->
                ShortUiItem(short, short.id in ids)
            }
        }.cachedIn(viewModelScope)

    private val _state = MutableStateFlow(ShortsState())
    val state: StateFlow<ShortsState> = _state


    fun onAction(action: ShortsActions) {
        when (action) {

            is ShortsActions.OnDeepLinkArrived -> {
                action.date?.let { isoDate ->
                    _beforeDate.value = addOneSecond(isoDate)        // <- triggers shorts re-load
                }
            }

            is ShortsActions.OnPostFavoriteClick -> {
                viewModelScope.launch {
if(action.shortUiItem.isFavorite){
    postsRepo.removeShortFromFavorites(action.shortUiItem.short.id)
}else{
    postsRepo.markShortAsFavorite(action.shortUiItem.short)
}

//                    action.shortUiItem.isFavorite = !action.shortUiItem.isFavorite
                               }

            }
else -> {}
        }
    }



//    fun observeFavoriteStatus() {
//        postsRepo.isPostFavorite(postId)
//            .onEach { isFavorite ->
//                _state.update {
//                    it.copy(
//                        isFavorite = isFavorite,
//                    )
//                }
//            }
//            .launchIn(viewModelScope)
//    }
}
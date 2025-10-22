package com.rawderm.taaza.today.bloger.ui.shorts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.data.paging.addOneSecond
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.bloger.domain.Short
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class ShortsViewModel(
    private val postsRepo: PostsRepo,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _beforeDate = MutableStateFlow(
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
    
    private val _language = MutableStateFlow<String?>(null)
    
    private val _scrollToTop = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val scrollToTop: SharedFlow<Unit> = _scrollToTop

    private val favoriteIds: Flow<Set<String>> =
        postsRepo.observeFavoriteShortIds()
        
    // Combined flow that considers both date and language
    private val dateAndLanguage = combine(_beforeDate, _language) { date, lang -> 
        date to lang
    }
        
    val shorts: Flow<PagingData<Short>> =
        dateAndLanguage
            .flatMapLatest { (date, lang) -> 
                if (lang != null) {
                    postsRepo.getShortsBeforeDateWithLanguage(date, lang)
                } else {
                    postsRepo.getShortsBeforeDateWithLanguage(date,languageManager.getLanguage())
                }
            }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = PagingData.empty()
            )
            
    val uiShorts: Flow<PagingData<ShortUiItem>> =
        shorts.combine(favoriteIds) { paging, ids ->
            var adCounter = AtomicInteger(1)          // 1-based position in the list

            paging
                .map { short ->
                    ShortUiItem.post(short, isFavorite = short.id in ids)
                }
                .insertSeparators { before, after ->
                    if (after == null || before == null) return@insertSeparators null
                    if (adCounter.get() % 3 == 0) {
                        Log.d("adcounter", adCounter.get().toString())
                        adCounter.incrementAndGet()
                        ShortUiItem.ad()
                    } else {
                        adCounter.incrementAndGet()
                        null
                    }
                }
        }.cachedIn(viewModelScope)

    private val _state = MutableStateFlow(ShortsState())
    val state: StateFlow<ShortsState> = _state


    fun onAction(action: ShortsActions) {
        when (action) {

            is ShortsActions.OnGetShortsByDate -> {
                action.date?.let { isoDate ->
                    _beforeDate.value = addOneSecond(isoDate)        // <- triggers shorts re-load
                    // Set the language if provided
                    action.lang?.let { _language.value = it }
                    _scrollToTop.tryEmit(Unit)
                }
            }

            is ShortsActions.OnPostFavoriteClick -> {
                viewModelScope.launch {
                    val item = action.shortUiItem
                    val id = item.short?.id ?: return@launch   // <- null-safety

                    if (item.isFavorite) postsRepo.removeShortFromFavorites(id)
                    else postsRepo.markShortAsFavorite(item.short)
                }

            }
            
            is ShortsActions.OnRefresh -> {
                viewModelScope.launch {
                    // Update state to show refreshing
                    _state.value = _state.value.copy(isRefreshing = true)
                    
                    // Reset the date to now and clear the language
                    resetDate()
                    _language.value = null // Reset language to use default behavior
                    
                    // Small delay to make the refresh indicator visible
                    delay(500)
                    
                    // Update state to hide refreshing
                    _state.value = _state.value.copy(isRefreshing = false)
                }
            }

            else -> {}
        }
    }
    fun onScrollToTopConsumed() {
        _scrollToTop.resetReplayCache()
    }

    fun resetDate() {
        _beforeDate.value =
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
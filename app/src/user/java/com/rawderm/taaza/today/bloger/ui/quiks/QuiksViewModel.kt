package com.rawderm.taaza.today.bloger.ui.quiks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.data.paging.addOneSecond
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class QuiksViewModel(
    private val postsRepo: PostsRepo,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _beforeDate = MutableStateFlow(
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
    private val lang = languageManager.currentLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), "")

    private val dateAndLanguage = combine(_beforeDate, lang) { date, language ->
        date to language
    }

    val pages: Flow<PagingData<Page>> = lang.flatMapLatest { language ->
        Log.d("HomeViewModel", "Creating pages flow with language: $language")
        postsRepo.getPages()
            .cachedIn(viewModelScope)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        PagingData.empty()
    )
    val quiks: Flow<PagingData<Post>> =
        dateAndLanguage
            .flatMapLatest { (date, lang) ->
                postsRepo.getQuiksBeforeDateWithLanguage(date, lang)
            }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = PagingData.empty()
            )
    val uiQuiks: Flow<PagingData<QuikUiItem>> =
        quiks.map { paging ->
            var adCounter = AtomicInteger(1)          // 1-based position in the list

            paging
                .map { short ->
                    QuikUiItem.post(short)
                }
                .insertSeparators { before, after ->
                    if (after == null || before == null) return@insertSeparators null
                    if (adCounter.get() % 4 == 0) {
                        Log.d("adcounter", adCounter.get().toString())
                        adCounter.incrementAndGet()
                        QuikUiItem.ad()
                    } else {
                        adCounter.incrementAndGet()
                        null
                    }
                }
        }.cachedIn(viewModelScope)
    private val _state = MutableStateFlow(QuiksState())
    val state: StateFlow<QuiksState> = _state
    fun onAction(action: QuiksActions) {
        when (action) {

            is QuiksActions.OnGetShortsByDate -> {
                action.date.let { isoDate ->
                    _beforeDate.value = addOneSecond(isoDate)        // <- triggers shorts re-load
                }
            }


            is QuiksActions.OnRefresh -> {
                viewModelScope.launch {
                    // Update state to show refreshing
                    _state.value = _state.value.copy(isRefreshing = true)
                    resetDate()
                    delay(500)
                    _state.value = _state.value.copy(isRefreshing = false)
                }
            }

            else -> {}
        }
    }


    fun resetDate() {
        _beforeDate.value =
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

}
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
class QuiksViewModel(private val postsRepo: PostsRepo,
                     private val languageManager: LanguageManager
) : ViewModel() {
    private val _beforeDate = MutableStateFlow(
        OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    )
        private val _language = MutableStateFlow<String?>(null)
    private val dateAndLanguage = combine(_beforeDate, _language) { date, lang ->
        date to lang
    }
    val quiks: Flow<PagingData<Post>> =
        dateAndLanguage
            .flatMapLatest { (date, lang) ->
                if (lang != null) {
                    postsRepo.getQuiksBeforeDateWithLanguage(date, lang)
                } else {
                    postsRepo.getQuiksBeforeDateWithLanguage(date,languageManager.getLanguage())
                }
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
                    if (adCounter.get() % 1 == 0) {
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
                    // Set the language if provided
                    action.lang?.let { _language.value = it }
                }
            }


            is QuiksActions.OnRefresh -> {
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


    fun resetDate() {
        _beforeDate.value =
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

}
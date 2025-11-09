package com.rawderm.taaza.today.bloger.ui.pageDetails


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rawderm.taaza.today.app.Route
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.core.domain.onError
import com.rawderm.taaza.today.core.domain.onSuccess
import com.rawderm.taaza.today.core.ui.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PageDetailsViewModel(
    private val postsRepo: PostsRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val pageId = savedStateHandle.toRoute<Route.PageDetails>().pageId
    private val _state = MutableStateFlow(PageDetailsState())
    val state: StateFlow<PageDetailsState> = _state.onStart {
//        fetchPageDetails()
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    fun onAction(action: PageDetailsActions) {
        when (action) {
            PageDetailsActions.OnBackClick -> {}
            is PageDetailsActions.OnSelectedPageChange -> {
                _state.value = _state.value.copy(page = action.page)
            }

            else -> {}
        }
    }

    init {          // ← always runs once
        fetchPageDetails()
    }

    fun fetchPageDetails() {
        if (pageId.isBlank()) return
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            postsRepo.getPage(pageId).onSuccess { result ->
                _state.value = _state.value.copy(
                    page = result,
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
}

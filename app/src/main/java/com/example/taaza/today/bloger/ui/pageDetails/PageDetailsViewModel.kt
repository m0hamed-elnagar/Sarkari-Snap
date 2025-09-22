package com.example.taaza.today.bloger.ui.pageDetails


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.taaza.today.bloger.domain.PostsRepo
import com.example.taaza.today.app.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class PageDetailsViewModel(
    private val postsRepo: PostsRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val pageId = savedStateHandle.toRoute<Route.PageDetails>().pageId
    private val _state = MutableStateFlow(PageDetailsState())
    val state: StateFlow<PageDetailsState> = _state.onStart {
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
}

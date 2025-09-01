package com.example.sarkarisnap.bloger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sarkarisnap.bloger.domain.Post
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.onFailure
import kotlin.text.clear

class HomeViewModel(
    val repo : PostsRepo
) : ViewModel() {
    private var cachedPosts = emptyList<Post>()
    private val _state = MutableStateFlow(
        HomeUiState())

        val state = _state.asStateFlow()
    init {
        loadPosts()
    }
    fun onAction(action: HomeActions){
        when(action){
            is HomeActions.OnSearchQueryChange -> {}
            is HomeActions.OnTabSelected -> {
                _state.value = _state.value.copy(selectedTabIndex = action.index)
            }
            is HomeActions.OnPostClick -> {}
            is HomeActions.OnPostFavoriteClick ->{}
        }
    }
private fun observePosts(){

}
    fun loadPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
             repo.getHomePosts() .onSuccess {posts ->
                 cachedPosts = posts
                 _state.update {
                     it.copy(
                         isLoading = false,
                         errorMessage = null,
                         posts = posts
                     )
                 }
             }.onError {error ->
                 _state.update {
                     it.copy(
                         isLoading = false,
                         errorMessage = error.toUiText(),
                         posts = emptyList()
                     )}

             }
        }
    }
}
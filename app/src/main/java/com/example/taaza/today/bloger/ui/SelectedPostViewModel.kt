package com.example.taaza.today.bloger.ui

import androidx.lifecycle.ViewModel
import com.example.taaza.today.bloger.domain.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectedPostViewModel : ViewModel() {
    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost
    fun selectPost(post: Post?) {
        _selectedPost.value = post
    }
}
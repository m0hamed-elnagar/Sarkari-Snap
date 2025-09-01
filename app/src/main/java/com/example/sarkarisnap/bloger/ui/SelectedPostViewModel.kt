package com.example.sarkarisnap.bloger.ui

import androidx.lifecycle.ViewModel
import com.example.sarkarisnap.bloger.domain.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectedPostViewModel : ViewModel() {
    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost
    fun selectPost(post: Post?) {
        _selectedPost.value = post
    }
}
package com.example.sarkarisnap.bloger.ui.postDetails

import com.example.sarkarisnap.bloger.domain.Post

data class PostDetailsState (
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val post: Post? = null,
)
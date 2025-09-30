package com.rawderm.taaza.today.bloger.ui.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.rawderm.taaza.today.bloger.domain.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val isPlaying: Boolean = false
)
enum class YouTubePlayerState { UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, CUED }

class ShortsViewModel : ViewModel() {
    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos
    private val fixedId = "uCAHNFRTh3w"
    val testPosts: Flow<PagingData<Post>> =
        Pager(PagingConfig(pageSize = 20)) {
            object : PagingSource<Int, Post>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
                    val page = params.key ?: 0
                    val pageSize = params.loadSize

                    // Fake items for testing
                    val start = page * pageSize
                    val end = minOf(start + pageSize, 100) // limit to 100 items
                    val data = (start until end).map { index ->
                        Post(
                            id = index.toString(),
                            title = "$fixedId - Item $index",
                            url = "",
                            description =  "",
                            content = "",
                            labels = emptyList(),
                            imageUrls = emptyList(),
                            videoUrl =fixedId,
                            date = "",
                            rowDate = ""
                        )
                    }

                    return LoadResult.Page(
                        data = data,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (end >= 100) null else page + 1
                    )
                }

                override fun getRefreshKey(state: PagingState<Int, Post>): Int? = null
            }
        }.flow.cachedIn(viewModelScope)
    private val _currentPlayingIndex = MutableStateFlow<Int?>(null)
    val currentPlayingIndex: StateFlow<Int?> = _currentPlayingIndex

    init {
        loadVideos()
    }

    private fun loadVideos() {
        val videoList = listOf(
            VideoItem("uCAHNFRTh3w", "Video 1", "Description for video 1"),

        )
        _videos.value = videoList
    }

    fun setCurrentPlayingIndex(index: Int?) {
        viewModelScope.launch {
            _currentPlayingIndex.value = index

            // Update playing states
            val updatedVideos = _videos.value.mapIndexed { i, video ->
                video.copy(isPlaying = i == index)
            }
            _videos.value = updatedVideos
        }
    }

    fun pauseAllVideos() {
        setCurrentPlayingIndex(null)
    }
}
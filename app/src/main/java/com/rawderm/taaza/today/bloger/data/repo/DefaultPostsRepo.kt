package com.rawderm.taaza.today.bloger.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.SQLiteException
import com.rawderm.taaza.today.bloger.data.database.FavoritePostDao
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.mappers.toPage
import com.rawderm.taaza.today.bloger.data.mappers.toPost
import com.rawderm.taaza.today.bloger.data.mappers.toPostEntity
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.data.paging.shortsPagingSource
import com.rawderm.taaza.today.bloger.data.paging.pagesPagingSource
import com.rawderm.taaza.today.bloger.data.paging.postsBeforeDatePagingSource
import com.rawderm.taaza.today.bloger.data.paging.postsPagingSource
import com.rawderm.taaza.today.bloger.data.paging.shortsBeforeDatePagingSource
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.EmptyResult
import com.rawderm.taaza.today.core.domain.Result
import com.rawderm.taaza.today.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultPostsRepo(
    private val remotePostDataSource: RemotePostDataSource,
    private val dao: FavoritePostDao
) : PostsRepo {


    override suspend fun getLabels(): Result<List<String>, DataError.Remote> {
        return remotePostDataSource.getUniqueLabels().map { dto ->
            val excludedLabels = setOf("shorts", "video", "test 1", "test")
            listOf("All") + dto.items
                .flatMap { it.labels }
                .filter { it.lowercase() !in excludedLabels }
                .distinct()      .distinct()
        }
    }


    override suspend fun getFavoritePosts(): Flow<List<Post>> {
        return dao.getAllFavoriteBook().map { entities ->
            entities.map { it.toPost() }
        }
    }

    override fun isPostFavorite(postId: String): Flow<Boolean> {
        return dao.getAllFavoriteBook().map { entities ->
            entities.any { it.id == postId }
        }
    }

    override suspend fun markPostAsFavorite(post: Post): EmptyResult<DataError.Local> {
        return try {

            dao.upsert(post.toPostEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removePostFromFavorites(postId: String) {
        return dao.deleteFavoriteBook(postId)
    }

    override fun getPagedPosts(label: String?): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 4, enablePlaceholders = false),
            pagingSourceFactory = {
                postsPagingSource(
                    remotePostDataSource,
                    if (label == "All") null else label
                )
            }
        ).flow
    }
    override fun getPagedShorts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = {
                shortsPagingSource(
                    remotePostDataSource)
            }
        ).flow
    }

    override fun getPages(): Flow<PagingData<Page>> {
        return Pager(
            config = PagingConfig(pageSize = 4, enablePlaceholders = false),
            pagingSourceFactory = {
                pagesPagingSource(
                    remotePostDataSource
                )
            }
        ).flow

    }

    override fun getPostsAfterDate(label: String?, afterDate: String?): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 6, enablePlaceholders = false),
            pagingSourceFactory = {
                postsBeforeDatePagingSource(
                    remotePostDataSource,
                    label,
                    afterDate
                )
            }
        ).flow

    }
 override fun getShortsBeforeDate( afterDate: String?): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 6, enablePlaceholders = false),
            pagingSourceFactory = {
                shortsBeforeDatePagingSource(
                    remotePostDataSource,
                    afterDate
                )
            }
        ).flow

    }

    override suspend fun getPage(pageId: String): Result<Page, DataError.Remote> =
        remotePostDataSource.getPage(pageId).map { it.toPage() }

    override suspend fun getPostById(postId: String): Result<Post, DataError.Remote> {
       return remotePostDataSource.getPost(postId).map { toDomain(it) }

    }


}
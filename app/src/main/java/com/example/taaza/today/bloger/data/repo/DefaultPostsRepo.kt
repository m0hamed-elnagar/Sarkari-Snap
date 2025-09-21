package com.example.taaza.today.bloger.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.SQLiteException
import com.example.taaza.today.bloger.data.database.FavoritePostDao
import com.example.taaza.today.bloger.data.dto.BloggerResponse
import com.example.taaza.today.bloger.data.mappers.toDomain
import com.example.taaza.today.bloger.data.mappers.toPost
import com.example.taaza.today.bloger.data.mappers.toPostEntity
import com.example.taaza.today.bloger.data.network.RemotePostDataSource
import com.example.taaza.today.bloger.data.paging.PostsPagingSource
import com.example.taaza.today.bloger.domain.Page
import com.example.taaza.today.bloger.domain.Post
import com.example.taaza.today.bloger.domain.PostsRepo
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultPostsRepo(
    private val remotePostDataSource: RemotePostDataSource,
    private val dao: FavoritePostDao
) : PostsRepo {

    override suspend fun getPosts(
        limit: Int,
        label: String?,
        pageToken: String?
    ): Result<Pair<List<Post>, String?>, DataError.Remote> {
        return remotePostDataSource.getPosts(limit, label, pageToken)
            .map { dto: BloggerResponse ->
                dto.items.map { toDomain(it) } to dto.nextPageToken
            }
    }

    override suspend fun getLabels(): Result<List<String>, DataError.Remote> {
        return remotePostDataSource.getUniqueLabels().map { dto ->
            listOf("All") + dto.items
                .flatMap { it.labels }
                .distinct()
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
                PostsPagingSource(
                    remotePostDataSource,
                    if (label == "All") null else label
                )
            }
        ).flow
    }

    override suspend fun getPages(): Result<List<Page>, DataError.Remote> {
        return remotePostDataSource.getPages().map { response ->
            response.items.map { Page(
                id = it.id,
                title = it.title,
                content = it.content,
                url = it.url
            ) }
        }
    }


}
package com.rawderm.taaza.today.bloger.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.SQLiteException
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.database.FavoritePostDao
import com.rawderm.taaza.today.bloger.data.database.ShortDao
import com.rawderm.taaza.today.bloger.data.mappers.toDomain
import com.rawderm.taaza.today.bloger.data.mappers.toPage
import com.rawderm.taaza.today.bloger.data.mappers.toPost
import com.rawderm.taaza.today.bloger.data.mappers.toPostEntity
import com.rawderm.taaza.today.bloger.data.mappers.toShort
import com.rawderm.taaza.today.bloger.data.mappers.toShortEntity
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.data.paging.QuiksBeforeDatePagingSource
import com.rawderm.taaza.today.bloger.data.paging.pagesPagingSource
import com.rawderm.taaza.today.bloger.data.paging.postsBeforeDatePagingSource
import com.rawderm.taaza.today.bloger.data.paging.postsPagingSource
import com.rawderm.taaza.today.bloger.data.paging.shortsBeforeDatePagingSource
import com.rawderm.taaza.today.bloger.data.paging.shortsBeforeDatePagingSourceWithLanguage
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.bloger.domain.Short
import com.rawderm.taaza.today.core.domain.DataError
import com.rawderm.taaza.today.core.domain.EmptyResult
import com.rawderm.taaza.today.core.domain.Result
import com.rawderm.taaza.today.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class DefaultPostsRepo(
    private val remotePostDataSource: RemotePostDataSource,
    private val postDao: FavoritePostDao,
    private val shortDao: ShortDao,
    private val languageDataStore: LanguageDataStore,
) : PostsRepo {


    override suspend fun getLabels(): Result<List<String>, DataError.Remote> {
        val lang = languageDataStore.getLanguageSync()

        return remotePostDataSource.getUniqueLabels(currentLang = lang).map { dto ->
            val excludedLabels =
                setOf("shorts", "video", "test 1", "test", "trending", "quik", "quiks", "quick")
            val isHindi = lang.equals("hi", ignoreCase = true)
            val canonicalOrder = if (isHindi) hindiOrder else englishOrder
            val allLabel = if (isHindi) "सभी" else "All"
            val orderLookup = canonicalOrder
                .mapIndexed { index, label -> label.lowercase(Locale.getDefault()) to index }
                .toMap()

            val remoteLabels = dto.items
                .flatMap { it.labels }
                .map { it.trim().lowercase(Locale.getDefault()) }
                .filter { it.isNotEmpty() && it !in excludedLabels }
                .distinct()

            val sortedLabels = remoteLabels.sortedWith(
                compareBy({ orderLookup[it] ?: Int.MAX_VALUE }, { it })
            )

            // 8. Prepend "All" / "सभी"
            (listOf(allLabel) + sortedLabels)
                .map { it.replaceFirstChar(Char::uppercase) }


        }
    }

    override fun getLabelsFlow(): Flow<List<String>> {
        return kotlinx.coroutines.flow.flow {
            val result = getLabels()
            if (result is Result.Success) {
                emit(result.data)
            } else {
                // Emit empty list in case of error
                emit(emptyList())
            }
        }
    }

    override suspend fun getFavoriteShorts(): Flow<List<Short>> {
        return shortDao.getAllShort().map { entities ->
            entities.map { it.toShort() }
        }
    }

    override fun isShortFavorite(shortId: String): Flow<Boolean> {
        return shortDao.getAllShort().map { entities ->
            entities.any { it.id == shortId }
        }
    }

    override fun observeFavoriteShortIds(): Flow<Set<String>> =
        shortDao.observeIds()
            .map { it.toSet() }

    override suspend fun markShortAsFavorite(short: Short): EmptyResult<DataError.Local> {
        return try {

            shortDao.upsert(short.toShortEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeShortFromFavorites(shortId: String) {
        return shortDao.deleteShort(shortId)
    }


    override suspend fun getFavoritePosts(): Flow<List<Post>> {
        return postDao.getAllFavoritePosts().map { entities ->
            entities.map { it.toPost() }
        }
    }

    override fun isPostFavorite(postId: String): Flow<Boolean> {
        return postDao.getAllFavoritePosts().map { entities ->
            entities.any { it.id == postId }
        }
    }

    override suspend fun markPostAsFavorite(post: Post): EmptyResult<DataError.Local> {
        return try {

            postDao.upsert(post.toPostEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removePostFromFavorites(postId: String) {
        return postDao.deleteFavoritePost(postId)
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

    override fun getShortsBeforeDate(afterDate: String?): Flow<PagingData<Short>> {
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

    override fun getShortsBeforeDateWithLanguage(
        afterDate: String?,
        language: String
    ): Flow<PagingData<Short>> {
        return Pager(
            config = PagingConfig(pageSize = 6, enablePlaceholders = false),
            pagingSourceFactory = {
                shortsBeforeDatePagingSourceWithLanguage(
                    remotePostDataSource,
                    afterDate,
                    language
                )
            }
        ).flow
    }

    override fun getQuiksBeforeDateWithLanguage(
        afterDate: String?,
        language: String
    ): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 6, enablePlaceholders = false),
            pagingSourceFactory = {
                QuiksBeforeDatePagingSource(
                    remotePostDataSource,
                    afterDate,
                    language
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

val englishOrder = listOf(
    "politics",
    "crime",
    "entertainment",
    "sports",
    "business",
    "tech",
    "science",
    "automobile",
    "education",
    "job news",
    "yojana"
)
val hindiOrder = listOf(
    "राजनीति",
    "अपराध",
    "मनोरंजन",
    "खेल",
    "व्यापार",
    "टेक्नोलॉजी",
    "विज्ञान",
    "ऑटोमोबाइल",
    "शिक्षा",
    "रोजगार समाचार",
    "सरकारी योजनाएं"
)
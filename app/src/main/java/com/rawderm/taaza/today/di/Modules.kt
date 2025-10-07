package com.rawderm.taaza.today.di


import android.util.Log
import androidx.room.Room
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.data.database.FavoritePostDataBase
import com.rawderm.taaza.today.bloger.data.network.KtorRemoteBlogDataSource
import com.rawderm.taaza.today.bloger.data.network.RemotePostDataSource
import com.rawderm.taaza.today.bloger.data.repo.DefaultPostsRepo
import com.rawderm.taaza.today.bloger.domain.PostsRepo
import com.rawderm.taaza.today.bloger.ui.SelectedPostViewModel
import com.rawderm.taaza.today.bloger.ui.home.HomeViewModel
import com.rawderm.taaza.today.bloger.ui.labeled.LabeledPostsViewModel
import com.rawderm.taaza.today.bloger.ui.pageDetails.PageDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.postDetails.PostDetailsViewModel
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import com.rawderm.taaza.today.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<HttpClientEngine> { OkHttp.create() }
    single { LanguageDataStore(get())    }
    single{     LanguageManager(get(), get()).apply {
        // Initialize on creation
        CoroutineScope(Dispatchers.IO).launch {
            initialize()
        }
    } }
    single<RemotePostDataSource> {
        val context = get<android.content.Context>()
        val blogId = context.getString(R.string.blogger_id)
        Log.d("urlTest", "blogid: $blogId ")
        KtorRemoteBlogDataSource(get(), blogId,get())
    }
    singleOf(::DefaultPostsRepo)
        .bind<PostsRepo>()
    single {
        Room.databaseBuilder(
            get(),
            FavoritePostDataBase::class.java,
            "Posts_db"
        ).fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<FavoritePostDataBase>().favoritePostDao }
    viewModelOf(::HomeViewModel)
    viewModelOf(::PostDetailsViewModel)
    viewModelOf(::PageDetailsViewModel)
    viewModelOf(::SelectedPostViewModel)
    viewModelOf(::ShortsViewModel)
    viewModelOf(::LabeledPostsViewModel)


}
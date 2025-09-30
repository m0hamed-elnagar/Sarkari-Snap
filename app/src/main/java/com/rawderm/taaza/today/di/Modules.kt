package com.plcoding.bookpedia.di


import androidx.room.Room
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
import com.plcoding.bookpedia.core.data.HttpClientFactory
import com.rawderm.taaza.today.bloger.ui.shorts.ShortsViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<HttpClientEngine> { OkHttp.create() }
    singleOf(::KtorRemoteBlogDataSource)
        .bind<RemotePostDataSource>()
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
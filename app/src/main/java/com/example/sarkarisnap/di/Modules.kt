package com.plcoding.bookpedia.di


import com.example.sarkarisnap.bloger.data.network.KtorRemoteBlogDataSource
import com.example.sarkarisnap.bloger.data.network.RemotePostDataSource
import com.example.sarkarisnap.bloger.data.repo.DefaultPostsRepo
import com.example.sarkarisnap.bloger.domain.PostsRepo
import com.example.sarkarisnap.bloger.ui.SelectedPostViewModel
import com.example.sarkarisnap.bloger.ui.labeled.LabeledPostsViewModel
import com.example.sarkarisnap.bloger.ui.home.HomeViewModel
import com.example.sarkarisnap.bloger.ui.postDetails.PostDetailsViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single{HttpClientFactory.create(get())}
    single<HttpClientEngine> { OkHttp.create () }
    singleOf(::KtorRemoteBlogDataSource)
        .bind<RemotePostDataSource>()
    singleOf(::DefaultPostsRepo)
        .bind<PostsRepo>()
//    single{
//        get<DatabaseFactory>().create()
//            .setDriver(BundledSQLiteDriver())
//            .build()
//    }
//    single{ get<FavoriteBookDataBase>().favoriteBookDao }
    viewModelOf(::HomeViewModel)
    viewModelOf(::PostDetailsViewModel)
    viewModelOf(::SelectedPostViewModel)
    viewModelOf(::LabeledPostsViewModel)



}
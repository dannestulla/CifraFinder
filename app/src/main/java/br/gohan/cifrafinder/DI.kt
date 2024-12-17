package br.gohan.cifrafinder

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import br.gohan.cifrafinder.data.CifraApi
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.domain.usecase.CifraScheduler
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {

    viewModel { (savedState: SavedStateHandle) ->
        MainViewModel(get(), get(), get())
    }

    factory {
        MainRepository(get())
    }

    factory {
        GoogleService(get(), get())
    }

    factory {
        SpotifyService(get(), get())
    }

    factory {
        get<Retrofit>().create(CifraApi::class.java)
    }

    single {
        provideRetrofit()
    }

    single {
        WorkManager.getInstance(androidApplication())
    }

    single {
        androidApplication().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    }

    factory {
        CifraScheduler(androidApplication(), get())
    }
}

private fun provideRetrofit() =
    Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

package br.gohan.cifrafinder

import androidx.activity.ComponentActivity
import androidx.work.WorkManager
import br.gohan.cifrafinder.data.CifraApi
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.domain.usecase.CifraScheduler
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.CifraViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {

    viewModel {
        CifraViewModel(get(), get())
    }

    factory {
        CifraRepository(get())
    }

    factory {
        GoogleService(get())
    }

    factory {
        SpotifyService(get())
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
        androidApplication().getSharedPreferences("sharedPref", ComponentActivity.MODE_PRIVATE)
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

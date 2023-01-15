package com.example.cifrafinder

import com.example.cifrafinder.data.CifraRepository
import com.example.cifrafinder.data.remote.CifraApi
import com.example.cifrafinder.model.CifraUseCase
import com.example.cifrafinder.presenter.login.LoginViewModel
import com.example.cifrafinder.presenter.webview.WebViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {

        viewModel {
            LoginViewModel(get())
        }

        viewModel {
            WebViewModel(get())
        }

        factory {
            CifraRepository(get())
        }

        factory {
            CifraUseCase(get())
        }

        factory {
            get<Retrofit>().create(CifraApi::class.java)
        }

        single{
            provideRetrofit()
        }
    }

    private fun provideRetrofit() =
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

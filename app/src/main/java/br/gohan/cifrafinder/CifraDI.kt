package br.gohan.cifrafinder

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.CifraApi
import br.gohan.cifrafinder.domain.CifraUseCase
import br.gohan.cifrafinder.presenter.MusicFetchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {

        viewModel {
            MusicFetchViewModel(get())
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

package br.gohan.cifrafinder.data

import br.gohan.cifrafinder.data.model.GoogleJson
import br.gohan.cifrafinder.data.model.SpotifyJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface CifraApi {
    @GET
    suspend fun getCurrentlyPlaying(
        @Url url: String,
        @Header("Authorization") myToken: String
    ): Response<SpotifyJson>

    @GET
    suspend fun getGoogleSearchResult(
        @Url url: String,
        @Query("key") key: String,
        @Query("cx") cx: String,
        @Query("q") q: String
    ): Response<GoogleJson>
}
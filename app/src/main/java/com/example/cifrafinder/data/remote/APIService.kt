package com.example.cifrafinder.data.remote

import com.example.cifrafinder.CifraConstants
import com.example.cifrafinder.data.remote.model.GoogleJson
import com.example.cifrafinder.data.remote.model.SpotifyJson
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService {
    @GET(CifraConstants.spotifyEndPoint)
    suspend fun getCurrentlyPlaying(@Header("Authorization") myToken: String): Response<SpotifyJson>

    @GET
    suspend fun getGoogleSearchResult(
        @Url url: String,
        @Query("key") key: String,
        @Query("cx") cx: String,
        @Query("q") q: String
    ): Response<GoogleJson>
}
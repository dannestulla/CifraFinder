package com.example.cifrafinder.data

import com.example.cifrafinder.data.remote.APIService

class CifraRepository(
    private val APIService: APIService
) {
    suspend fun getCurrentlyPlaying(spotifyToken: String) =
        APIService.getCurrentlyPlaying("Bearer $spotifyToken")

    suspend fun getGoogleSearchResult(
        url: String,
        apiKey1: String,
        apiKey2: String,
        searchInput: String
    ) =
        APIService.getGoogleSearchResult(url, apiKey1, apiKey2, searchInput)
}
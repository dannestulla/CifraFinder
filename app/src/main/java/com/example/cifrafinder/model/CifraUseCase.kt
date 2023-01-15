package com.example.cifrafinder.model

import com.example.cifrafinder.data.CifraRepository
import com.example.cifrafinder.data.remote.model.SpotifyJson

class CifraUseCase(
    private val cifraRepository: CifraRepository
) {
    suspend fun getCurrentlyPlaying(spotifyToken : String) =
        cifraRepository.getCurrentlyPlaying(spotifyToken)

    suspend fun getGoogleSearchResult(apiKey1: String, apiKey2: String, searchInput: String) =
        cifraRepository.getGoogleSearchResult(apiKey1, apiKey2, searchInput)

    fun createSearchString(body: SpotifyJson?): String {
        val responseBody = body?.item
        val artistName = responseBody?.artists?.first()?.name
        val songName = responseBody?.name
        return "$artistName $songName"
    }

    fun filterSearch(artistAndSong: String) =
        artistAndSong.apply {
            replace("Ao Vivo", "", ignoreCase = true)
            replace("-", "")
        }
}

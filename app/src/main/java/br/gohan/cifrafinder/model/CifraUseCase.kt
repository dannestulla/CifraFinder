package br.gohan.cifrafinder.model

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.remote.model.SpotifyJson

class CifraUseCase(
    private val cifraRepository: CifraRepository,
) {
    suspend fun getCurrentlyPlaying(spotifyToken : String) =
        cifraRepository.getCurrentlyPlaying(spotifyToken)

    suspend fun getGoogleSearchResult(apiKey1: String, apiKey2: String, searchInput: String) =
        cifraRepository.getGoogleSearchResult(apiKey1, apiKey2, searchInput)

    fun createSearchString(body: SpotifyJson?): String {
        val responseBody = body?.item
        val artistName = responseBody?.artists?.first()?.name
        val songName = responseBody?.name
        val result = "$artistName $songName"
        return "$result"
    }

    fun filterSearch(artistAndSong: String) =
        artistAndSong.apply {
            replace("Ao Vivo", "", ignoreCase = true)
            replace("-", "")
        }
}

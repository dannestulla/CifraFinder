package br.gohan.cifrafinder.domain

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import okhttp3.ResponseBody

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
        return " $result "
    }

    fun filterSearch(artistAndSong: String) =
        artistAndSong.apply {
            replace("Ao Vivo", "", ignoreCase = true)
            replace("-", "")
        }

    fun createCurrentSongData(searchString: String, body: SpotifyJson?): CurrentSongModel {
        val durationMs = body?.item?.durationMs ?: 0L
        val progressMs = body?.progressMs ?: 0L
        return CurrentSongModel(
            searchString,
            durationMs,
            progressMs
        )
    }

    fun handleSpotifyError(code: Int, errorBody: ResponseBody?) : String {
            return when (code) {
                204 -> "Nenhuma música está sendo tocada no momento"
                else -> "Error code: $code, ${errorBody.toString()}"
            }
    }
}

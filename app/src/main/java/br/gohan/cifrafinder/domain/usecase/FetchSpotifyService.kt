package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import retrofit2.Response

class FetchSpotifyService(
    private val repository: CifraRepository
) {
    suspend fun invoke(spotifyToken : String): CurrentSongModel? {
        val response = repository.getCurrentlyPlaying(spotifyToken)
        return handleResponse(response)
    }

    private fun handleResponse(response: Response<SpotifyJson>): CurrentSongModel? {
        val responseSuccessful = response.isSuccessful && response.body() != null
        return if (responseSuccessful) {
            val searchString = setSearchString(response.body())
            setCurrentSongData(searchString, response.body())
        } else {
            null
        }
    }

    private fun setSearchString(body: SpotifyJson?): String {
        val responseBody = body?.item
        val artistName = responseBody?.artists?.first()?.name
        val songName = responseBody?.name
        val result = "$artistName $songName"
        return " $result "
    }

    private fun setCurrentSongData(searchString: String, body: SpotifyJson?): CurrentSongModel {
        val durationMs = body?.item?.durationMs ?: 0L
        val progressMs = body?.progressMs ?: 0L
        return CurrentSongModel(
            searchString,
            durationMs,
            progressMs
        )
    }
}
package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.Response

class SpotifyService(
    private val repository: CifraRepository
) : FetchService <String, SongData?> {
    override suspend fun invoke(params: String): SongData? {
            val response = repository.getCurrentlyPlaying(params)
            return handleResponse(response)
        }

    override fun handleResponse(response: Response<*>): SongData? {
        val responseSuccessful = response.isSuccessful && response.body() != null
        return if (responseSuccessful) {
            createModel(response.body())
        } else {
            if (!BuildConfig.DEBUG) {
                Firebase.crashlytics.log("Spotify response error: ${response.raw()}")
            }
            return null
        }
    }

    override fun <T> createModel(apiModel: T): SongData {
        val searchString = setSearchString(apiModel as SpotifyJson)
        val durationMs = apiModel.item.durationMs
        val progressMs = apiModel.progressMs
        return SongData(
            searchString,
            durationMs,
            progressMs
        )
    }

    fun setSearchString(body: SpotifyJson): String {
        val responseBody = body.item
        val artistName = responseBody.artists.first().name
        val songName = responseBody.name
        return addSpaceAroundSearchString("$songName - $artistName")
    }

    /**
     * This is done so the search at Cifra Club behaves less buggy
     */
    private fun addSpaceAroundSearchString(searchString: String) = " $searchString "
}
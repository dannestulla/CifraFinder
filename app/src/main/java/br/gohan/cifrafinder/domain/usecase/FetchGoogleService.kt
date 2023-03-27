package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.Response

class FetchGoogleService(
    private val repository: CifraRepository
) {
    suspend fun invoke(searchQuery: String): String? {
        val result = repository.getGoogleSearchResult(
            CifraConstants.googleApiKey1,
            CifraConstants.searchEngineId,
            filterSearch(searchQuery)
        )
        return handleResult(result)
    }

    fun filterSearch(artistAndSong: String): String {
        return artistAndSong.replace("Ao Vivo", "", ignoreCase = true)
            .replace("-", "", ignoreCase = true)
            .replace("  ", " ")
    }

    fun handleResult(googleResponse: Response<GoogleJson>): String? {
        return if (googleResponse.isSuccessful) {
            googleResponse.body()?.items?.first()?.link
        } else {
            Firebase.crashlytics.log("Google response error: ${googleResponse.raw()}")
            return null
        }
    }
}
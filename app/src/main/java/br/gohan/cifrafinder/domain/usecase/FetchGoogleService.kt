package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import retrofit2.Response

class FetchGoogleService(
    private val repository: CifraRepository
) {
    suspend fun invoke(searchQuery: String): String? {
        val result = repository.getGoogleSearchResult(
            CifraConstants.googleApiKey1,
            CifraConstants.googleApiKey2,
            searchQuery
        )
        val query = handleResult(result)
        return query?.let { filterSearch(it) }
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
            null
        }
    }
}
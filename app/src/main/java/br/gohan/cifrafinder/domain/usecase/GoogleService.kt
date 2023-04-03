package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.Response

class GoogleService(
    private val repository: CifraRepository
) : FetchService<String, String?>{

    override suspend fun invoke(params: String): String? {
        val result = repository.getGoogleSearchResult(
            CifraConstants.googleApiKey1,
            CifraConstants.searchEngineId,
            filterSearch(params)
        )
        return handleResponse(result)
    }

    override fun handleResponse(response: Response<*>): String? {
        return if (response.isSuccessful) {
            createModel(response.body())
        } else {
            if (!BuildConfig.DEBUG) {
                Firebase.crashlytics.log("Google response error: ${response.raw()}")
            }
            return null
        }
    }

    override fun <T> createModel(apiModel: T): String? {
        val responseBody = apiModel as? GoogleJson
        return responseBody?.items?.first()?.link
    }

    fun filterSearch(artistAndSong: String): String {
        return artistAndSong.replace("Ao Vivo", "", ignoreCase = true)
            .replace("-", "", ignoreCase = true)
            .replace("  ", " ")
    }
}
package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Response

class GoogleService(
    private val repository: CifraRepository
) {
    suspend fun getTablatureLink(songName: String) = withContext(Dispatchers.Default) {
        val query = async {
            invoke(songName)
        }
        return@withContext query
    }.await()
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
            if (!BuildConfig.DEBUG) {
                Firebase.crashlytics.log("Google response error: ${googleResponse.raw()}")
            }
            return null
        }
    }
}
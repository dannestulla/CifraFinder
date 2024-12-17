package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import retrofit2.Response

class GoogleService(
    private val repository: MainRepository,
    private val context: Context
) : FetchService<Params, String> {

    override suspend fun invoke(params: Params): Result<String> {
        return try {
            val result = repository.getGoogleSearchResult(
                BuildConfig.GOOGLE_API_KEY,
                BuildConfig.SEARCH_ENGINE_ID,
                filterSearch(params)
            )
            Result.success(handleResponse(result))
        } catch (error: Throwable) {
            Result.failure(
                Throwable(error.message)
            )
        }
    }

    override fun handleResponse(response: Response<*>): String {
        return if (response.isSuccessful) {
            createModel(response.body())
        } else {
            throw Throwable("${response.code()} - ${response.message()}")
        }
    }

    override fun <T> createModel(apiModel: T): String {
        val responseBody = apiModel as? GoogleJson
        return responseBody?.items?.firstOrNull()?.link ?: throw Throwable(context.getString(R.string.response_error_google))
    }

    fun filterSearch(artistAndSong: String): String {
        return artistAndSong.replace("Ao Vivo", "", ignoreCase = true)
            .replace("-", "", ignoreCase = true)
            .replace("  ", " ")
    }
}
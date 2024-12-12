package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.GoogleJson
import retrofit2.Response

class GoogleService(
    private val repository: MainRepository
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
                Throwable("Erro na resposta: ${error.message}")
            )
        }
    }

    override fun handleResponse(response: Response<*>): String {
        return if (response.isSuccessful) {
            createModel(response.body())
        } else {
            throw Throwable("Erro na resposta: ${response.code()} - ${response.message()}")
        }
    }

    override fun <T> createModel(apiModel: T): String {
        val responseBody = apiModel as GoogleJson
        return responseBody.items.first().link
    }

    fun filterSearch(artistAndSong: String): String {
        return artistAndSong.replace("Ao Vivo", "", ignoreCase = true)
            .replace("-", "", ignoreCase = true)
            .replace("  ", " ")
    }
}
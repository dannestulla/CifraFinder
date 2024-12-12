package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import retrofit2.Response

class SpotifyService(
    private val repository: MainRepository
) : FetchService<Params, SongData> {
    override suspend fun invoke(params: Params): Result<SongData> {
        return try {
            val response = repository.getCurrentlyPlaying(params)
            Result.success(handleResponse(response))
        } catch (error: Throwable) {
            Result.failure(
                Throwable("Erro na resposta: ${error.message}")
            )
        }
    }

    override fun handleResponse(response: Response<*>): SongData {
        return if (response.isSuccessful && response.body() != null) {
            createModel(response.body())
        } else {
            throw Throwable("Erro na resposta: ${response.code()} - ${response.message()}")
        }
    }

    override fun <T> createModel(apiModel: T): SongData {
        val searchString = setSearchString(apiModel as SpotifyJson)
        val durationMs = apiModel.item?.durationMs
        val progressMs = apiModel.progressMs
        return SongData(
            searchString,
            durationMs,
            progressMs
        )
    }

    fun setSearchString(body: SpotifyJson): String {
        val responseBody = body.item
        val artistName = responseBody?.artists?.firstOrNull()?.name
        val songName = responseBody?.name
        return addSpaceAroundSearchString("$songName - $artistName")
    }

    /**
     * This is done so the search at Cifra Club behaves less buggy
     */
    private fun addSpaceAroundSearchString(searchString: String) = " $searchString "
}
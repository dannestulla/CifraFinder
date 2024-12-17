package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import retrofit2.Response

class SpotifyService(
    private val repository: MainRepository,
    private val context: Context
) : FetchService<Params, SongData> {
    override suspend fun invoke(params: Params): Result<SongData> {
        return try {
            val response = repository.getCurrentlyPlaying(params)
            Result.success(handleResponse(response))
        } catch (error: Throwable) {
            Result.failure(
                Throwable(
                    error.message
                )
            )
        }
    }

    override fun handleResponse(response: Response<*>): SongData {
        return if (response.isSuccessful
            && response.body() != null
        ) {
            createModel(response.body())
        } else {
            with (context) {
                throw Throwable(
                    getString(
                        R.string.response_error_spotify,
                        if (response.body() == null) getString(R.string.response_error_spotify_body_null) else
                            "${response.code()} - ${response.message()}"
                    )
                )
            }

        }
    }

    override fun <T> createModel(apiModel: T): SongData {
        val searchString = setSearchString(apiModel as SpotifyJson)
        val durationMs = apiModel.item?.duration_ms
        val progressMs = apiModel.progress_ms
        val songImage = apiModel.item?.album?.images?.firstOrNull()?.url
        return SongData(
            searchString,
            durationMs,
            progressMs,
            songImage
        )
    }

    fun setSearchString(body: SpotifyJson): String {
        val responseBody = body.item
        val artistName = responseBody?.artists?.firstOrNull()?.name
        if (artistName.isNullOrEmpty()) throw Throwable(context.getString(R.string.music_not_found))
        val songName = responseBody.name
        return addSpaceAroundSearchString("$songName - $artistName")
    }

    /**
     * This is done so the search at Cifra Club behaves less buggy
     */
    private fun addSpaceAroundSearchString(searchString: String) = " $searchString "
}
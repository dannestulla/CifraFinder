package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import android.util.Log
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.SongData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Response

class SpotifyService(
    private val repository: MainRepository,
    private val context: Context
) : FetchService<Params, SongData> {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override suspend fun invoke(params: Params): Result<SongData> {
        Log.d(TAG, "Fetching currently playing song")
        crashlytics.log("SpotifyService: Fetching currently playing")

        return try {
            val response = repository.getCurrentlyPlaying(params)
            Log.d(TAG, "Spotify response received: ${response.isSuccessful}")
            Result.success(handleResponse(response))
        } catch (error: Throwable) {
            Log.e(TAG, "Error fetching Spotify data", error)
            crashlytics.log("SpotifyService error: ${error.javaClass.simpleName} - ${error.message}")
            crashlytics.recordException(error)
            Result.failure(
                Throwable("${error.javaClass.simpleName}: ${error.message}", error)
            )
        }
    }

    override fun handleResponse(response: Response<*>): SongData {
        return if (response.isSuccessful && response.body() != null) {
            Log.d(TAG, "Response body type: ${response.body()?.javaClass?.simpleName}")
            try {
                createModel(response.body())
            } catch (error: Throwable) {
                Log.e(TAG, "Error creating model from response", error)
                crashlytics.log("SpotifyService createModel error: ${error.javaClass.simpleName}")
                crashlytics.recordException(error)
                throw error
            }
        } else {
            val errorMsg = if (response.body() == null) {
                context.getString(R.string.response_error_spotify_body_null)
            } else {
                "${response.code()} - ${response.message()}"
            }
            Log.e(TAG, "Spotify response error: $errorMsg")
            crashlytics.log("SpotifyService response error: $errorMsg")
            throw Throwable(context.getString(R.string.response_error_spotify, errorMsg))
        }
    }

    override fun <T> createModel(apiModel: T): SongData {
        val searchString = setSearchString(apiModel as SpotifyJson)
        val durationMs = apiModel.item?.duration_ms
        val progressMs = apiModel.progress_ms
        val songImage = apiModel.item?.album?.images?.firstOrNull()?.url
        Log.d(TAG, "Created SongData: $searchString")
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
        if (artistName.isNullOrEmpty()) {
            Log.w(TAG, "Artist name is null or empty")
            throw Throwable(context.getString(R.string.music_not_found))
        }
        val songName = responseBody.name
        return addSpaceAroundSearchString("$songName - $artistName")
    }

    /**
     * This is done so the search at Cifra Club behaves less buggy
     */
    private fun addSpaceAroundSearchString(searchString: String) = " $searchString "

    companion object {
        private const val TAG = "SpotifyService"
    }
}
package br.gohan.cifrafinder.domain.usecase

import android.content.Context
import android.util.Log
import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.data.MainRepository
import br.gohan.cifrafinder.data.model.SerpApiResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Response

class GoogleService(
    private val repository: MainRepository,
    private val context: Context
) : FetchService<Params, String> {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override suspend fun invoke(params: Params): Result<String> {
        val searchQuery = filterSearch(params) + " cifraclub"
        Log.d(TAG, "Starting search for: $searchQuery")
        crashlytics.log("GoogleService: Starting search for: $searchQuery")

        return try {
            val result = repository.getSerperSearchResult(
                BuildConfig.SERP_API_KEY,
                searchQuery
            )
            Log.d(TAG, "Search response received: ${result.isSuccessful}")
            Result.success(handleResponse(result))
        } catch (error: Throwable) {
            Log.e(TAG, "Error during search", error)
            crashlytics.log("GoogleService error: ${error.javaClass.simpleName} - ${error.message}")
            crashlytics.recordException(error)
            Result.failure(
                Throwable("${error.javaClass.simpleName}: ${error.message}", error)
            )
        }
    }

    override fun handleResponse(response: Response<*>): String {
        return if (response.isSuccessful) {
            Log.d(TAG, "Response body type: ${response.body()?.javaClass?.simpleName}")
            try {
                createModel(response.body())
            } catch (error: Throwable) {
                Log.e(TAG, "Error creating model from response", error)
                crashlytics.log("GoogleService createModel error: ${error.javaClass.simpleName}")
                crashlytics.recordException(error)
                throw error
            }
        } else {
            val errorMsg = "${response.code()} - ${response.message()}"
            Log.e(TAG, "Response error: $errorMsg")
            crashlytics.log("GoogleService response error: $errorMsg")
            throw Throwable(errorMsg)
        }
    }

    override fun <T> createModel(apiModel: T): String {
        val responseBody = apiModel as? SerpApiResponse
        Log.d(TAG, "Parsing response: organic results count = ${responseBody?.organic?.size}")
        return responseBody?.organic?.firstOrNull()?.link
            ?: throw Throwable(context.getString(R.string.response_error_google))
    }

    fun filterSearch(artistAndSong: String): String {
        return artistAndSong.replace("Ao Vivo", "", ignoreCase = true)
            .replace("-", "", ignoreCase = true)
            .replace("  ", " ")
    }

    companion object {
        private const val TAG = "GoogleService"
    }
}
package br.gohan.cifrafinder.data

import android.util.Log
import br.gohan.cifrafinder.data.model.SerperRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainRepository(
    private val cifraApi: CifraApi
) {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    suspend fun getCurrentlyPlaying(spotifyToken: String) = try {
        Log.d(TAG, "Calling Spotify API")
        cifraApi.getCurrentlyPlaying(spotifyBaseUrl + spotifyEndPoint, "Bearer $spotifyToken")
    } catch (e: Exception) {
        Log.e(TAG, "Spotify API call failed", e)
        crashlytics.log("Repository: Spotify API error - ${e.javaClass.simpleName}")
        crashlytics.recordException(e)
        throw e
    }

    suspend fun getGoogleSearchResult(
        apiKey1: String,
        searchEngineId: String,
        searchInput: String
    ) = try {
        Log.d(TAG, "Calling Google Search API")
        cifraApi.getGoogleSearchResult(googleSearchUrl, apiKey1, searchEngineId, searchInput)
    } catch (e: Exception) {
        Log.e(TAG, "Google Search API call failed", e)
        crashlytics.log("Repository: Google Search API error - ${e.javaClass.simpleName}")
        crashlytics.recordException(e)
        throw e
    }

    suspend fun getSerperSearchResult(
        apiKey: String,
        searchInput: String
    ) = try {
        Log.d(TAG, "Calling Serper API for: $searchInput")
        cifraApi.getSerperSearchResult(
            serperUrl,
            apiKey,
            SerperRequest(q = searchInput)
        )
    } catch (e: Exception) {
        Log.e(TAG, "Serper API call failed", e)
        crashlytics.log("Repository: Serper API error - ${e.javaClass.simpleName}")
        crashlytics.recordException(e)
        throw e
    }

    companion object {
        private const val TAG = "MainRepository"
        const val spotifyBaseUrl = "https://api.spotify.com/"
        const val spotifyEndPoint = "v1/me/player/currently-playing"
        const val googleSearchUrl = "https://customsearch.googleapis.com/customsearch/v1/"
        const val serperUrl = "https://google.serper.dev/search"
    }
}
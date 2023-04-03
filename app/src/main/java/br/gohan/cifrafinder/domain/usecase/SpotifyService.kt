package br.gohan.cifrafinder.domain.usecase

import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.data.CifraRepository
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.ScreenState
import br.gohan.cifrafinder.domain.model.SongData
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Response

class SpotifyService(
    private val repository: CifraRepository
) {
    suspend fun getCurrentPlaying(dataState: DataState) = withContext(Dispatchers.Default) {
        val songData = async {
            invoke(dataState.spotifyToken)
        }
        return@withContext songData
    }.await()

    suspend fun invoke(spotifyToken: String): SongData? {
        val response = repository.getCurrentlyPlaying(spotifyToken)
        return handleResponse(response)
    }

    fun handleResponse(response: Response<SpotifyJson>): SongData? {
        val responseSuccessful = response.isSuccessful && response.body() != null
        return if (responseSuccessful) {
            val searchString = setSearchString(response.body())
            setCurrentSongData(searchString, response.body())
        } else {
            if (!BuildConfig.DEBUG) {
                Firebase.crashlytics.log("Spotify response error: ${response.raw()}")
            }
            return null
        }
    }

    fun setSearchString(body: SpotifyJson?): String {
        val responseBody = body?.item
        val artistName = responseBody?.artists?.first()?.name
        val songName = responseBody?.name
        return addBlankSpaceAroundSearchString("$artistName $songName")
    }

    /**
     * This is done so the search at Cifra Club behaves less buggy
     */
    private fun addBlankSpaceAroundSearchString(searchString: String) = " $searchString "

    fun setCurrentSongData(searchString: String, body: SpotifyJson?): SongData {
        val durationMs = body?.item?.durationMs ?: 0L
        val progressMs = body?.progressMs ?: 0L
        return SongData(
            searchString,
            durationMs,
            progressMs
        )
    }

    fun isSongDataValid(songData: SongData?, musicFetchJob: Job?) : Boolean {
        if (songData == null) {
            musicFetchJob?.cancel()
            return false
        }
        return true
    }
}
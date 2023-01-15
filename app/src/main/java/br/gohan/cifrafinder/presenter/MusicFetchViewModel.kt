package br.gohan.cifrafinder.presenter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.remote.model.GoogleJson
import br.gohan.cifrafinder.data.remote.model.SpotifyJson
import br.gohan.cifrafinder.model.CifraUseCase
import br.gohan.cifrafinder.model.CurrentSong
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class MusicFetchViewModel(
    private val cifraUseCase: CifraUseCase
) : ViewModel() {
    private var _toastMessage = MutableLiveData<String>()
    var toastMessage: LiveData<String> = _toastMessage

    private var _searchUrl = MutableLiveData<String?>()
    var searchUrl: LiveData<String?> = _searchUrl

    private var _spotifyToken = MutableLiveData<String>()
    var spotifyToken: LiveData<String> = _spotifyToken

    private var _currentSong = MutableLiveData<CurrentSong>()
    var currentSong: LiveData<CurrentSong> = _currentSong

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            _spotifyToken.value?.let {
                manageSpotifyResponse(cifraUseCase.getCurrentlyPlaying(it))
            }
        }
    }

    private fun manageSpotifyResponse(response: Response<SpotifyJson>) {
        val responseSuccessful = response.isSuccessful && response.body() != null
        if (responseSuccessful) {
            val currentItem = response.body()
            val durationMs = currentItem?.item?.durationMs?.toLong() ?: 0L
            val progressMs = currentItem?.progressMs?.toLong()?: 0L
            val searchString = cifraUseCase.createSearchString(response.body())
            val currentSong = CurrentSong(
                searchString,
                durationMs,
                progressMs
            )
            _currentSong.postValue(currentSong)
        } else {
            handleSpotifyError(response.code(), response.errorBody())
        }
    }

    private fun handleSpotifyError(code: Int, errorBody: ResponseBody?) {
        val error = when (code) {
            204 -> "Nenhuma música está sendo tocada no momento"
            else -> "Error code: $code, ${errorBody.toString()}"
        }
        _toastMessage.postValue(error)
    }

    fun setSpotifyToken(accessToken: String) = _spotifyToken.postValue(accessToken)

    fun getSongUrl(artistAndSong: String) {
        val searchQuery = cifraUseCase.filterSearch(artistAndSong)
        _toastMessage.postValue("Procurando por $searchQuery")
        fetchApiResponse(searchQuery)
    }

    private fun fetchApiResponse(searchQuery: String) {
        viewModelScope.launch {
            val response = cifraUseCase.getGoogleSearchResult(
                CifraConstants.googleApiKey1,
                CifraConstants.googleApiKey2,
                searchQuery
            )
            handleGoogleResponse(response)
        }
    }

    private fun handleGoogleResponse(googleResponse: Response<GoogleJson>) {
        if (googleResponse.isSuccessful) {
            _searchUrl.postValue(googleResponse.body()?.items?.first()?.link)
        } else {
            _toastMessage.postValue(googleResponse.message())
            Log.e(javaClass.simpleName, googleResponse.errorBody().toString())
        }
    }
}
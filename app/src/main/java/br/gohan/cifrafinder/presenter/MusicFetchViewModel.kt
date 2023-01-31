package br.gohan.cifrafinder.presenter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.model.GoogleJson
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.CifraUseCase
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class MusicFetchViewModel(
    private val cifraUseCase: CifraUseCase
) : ViewModel() {
    private var _toastMessage = MutableLiveData<String>()
    var toastMessage: LiveData<String> = _toastMessage

    private var _searchUrl = MutableStateFlow<String>("")
    var searchUrl = _searchUrl.asStateFlow()

    private var _spotifyToken = MutableLiveData<String>()
    var spotifyToken: LiveData<String> = _spotifyToken

    private var _currentSongModel = MutableLiveData<CurrentSongModel>()
    var currentSongModel: LiveData<CurrentSongModel> = _currentSongModel

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
            val searchString = cifraUseCase.createSearchString(response.body())
            val currentSongData = cifraUseCase.createCurrentSongData(searchString, response.body())
            _currentSongModel.postValue(currentSongData)
        } else {
            val error = cifraUseCase.handleSpotifyError(
                response.code(), response.errorBody()
            )
            _toastMessage.postValue(error)
        }
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
            googleResponse.body()?.items?.first()?.link?.let {
                _searchUrl.value = it
            }
        } else {
            _toastMessage.postValue(googleResponse.message())
            Log.e(javaClass.simpleName, googleResponse.errorBody().toString())
        }
    }
}
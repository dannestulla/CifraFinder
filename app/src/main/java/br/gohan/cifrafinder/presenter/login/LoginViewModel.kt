package br.gohan.cifrafinder.presenter.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.data.remote.model.SpotifyJson
import br.gohan.cifrafinder.model.CifraUseCase
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class LoginViewModel(
    private val cifraUseCase: CifraUseCase
) : ViewModel() {

    private var _toastMessage = MutableLiveData<String>()
    var toastMessage: LiveData<String> = _toastMessage

    private var _spotifyToken = MutableLiveData<String>()
    var spotifyToken: LiveData<String> = _spotifyToken

    private var _currentlyPlaying = MutableLiveData<String>()
    var currentlyPlaying: LiveData<String> = _currentlyPlaying

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            _spotifyToken.value?.let {
                manageSpotifyResponse(cifraUseCase.getCurrentlyPlaying(it))
            }
        }
    }

    private fun manageSpotifyResponse(spotifyResponse: Response<SpotifyJson>) {
        if (spotifyResponse.isSuccessful && spotifyResponse.body() != null) {
            val searchString = cifraUseCase.createSearchString(spotifyResponse.body())
            _currentlyPlaying.postValue(searchString)
        } else {
            handleSpotifyResponseError(spotifyResponse.code(), spotifyResponse.errorBody())
        }
    }

    private fun handleSpotifyResponseError(code: Int, errorBody: ResponseBody?) {
        val error = when (code) {
            204 -> "Nenhuma música está sendo tocada no momento"
            else -> "Error code: $code, ${errorBody.toString()}"
        }
        _toastMessage.postValue(error)
    }

    fun setSpotifyToken(accessToken: String) = _spotifyToken.postValue(accessToken)
}

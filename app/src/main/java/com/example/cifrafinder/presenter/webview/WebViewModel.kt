package com.example.cifrafinder.presenter.webview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cifrafinder.CifraConstants
import com.example.cifrafinder.CifraConstants.googleSearchUrl
import com.example.cifrafinder.data.remote.model.GoogleJson
import com.example.cifrafinder.model.CifraUseCase
import kotlinx.coroutines.launch
import retrofit2.Response

class WebViewModel(
    private val cifraUseCase: CifraUseCase
) : ViewModel() {

    private var _searchUrl = MutableLiveData<String>()
    var searchUrl: LiveData<String> = _searchUrl

    private var _toastMessage = MutableLiveData<String>()
    var toastMessage: LiveData<String> = _toastMessage

    fun getSongUrl(artistAndSong: String) {
        _toastMessage.postValue("Procurando por $artistAndSong")
        viewModelScope.launch {
            try {
                val response = cifraUseCase.getGoogleSearchResult(
                    googleSearchUrl,
                    CifraConstants.googleApiKey1,
                    CifraConstants.googleApiKey2,
                    artistAndSong
                )
                manageGoogleResponse(response)
            } catch (ex: Exception) {
                Log.e("searchResult", ex.toString())
            }
        }
    }

    private fun manageGoogleResponse(googleResponse: Response<GoogleJson>) {
        if (googleResponse.isSuccessful) {
            _searchUrl.postValue(googleResponse.body()?.items?.first()?.link)
        } else {
            _toastMessage.postValue(googleResponse.message())
            Log.e(javaClass.simpleName, googleResponse.errorBody().toString())
        }
    }
}


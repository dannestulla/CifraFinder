package com.example.cifrafinder.presenter.webview

import android.util.Log
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
    var searchUrl = _searchUrl

    fun getSongUrl(artistAndSong: String) = viewModelScope.launch {
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

    private fun manageGoogleResponse(googleResponse: Response<GoogleJson>) {
        if (googleResponse.isSuccessful) {
            _searchUrl.postValue(googleResponse.body()?.items?.first()?.link)
        } else {
            Log.e(javaClass.simpleName, googleResponse.errorBody().toString())
        }
    }
}

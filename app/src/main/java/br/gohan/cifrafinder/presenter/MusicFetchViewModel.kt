package br.gohan.cifrafinder.presenter

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.data.model.GoogleJson
import br.gohan.cifrafinder.data.model.SpotifyJson
import br.gohan.cifrafinder.domain.CifraScheduler
import br.gohan.cifrafinder.domain.CifraUseCase
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.koin.android.ext.koin.androidApplication
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MusicFetchViewModel(
    private val cifraUseCase: CifraUseCase
) : ViewModel() {
    private var _currentStage = MutableStateFlow(1)
    var currentStage =  _currentStage.asStateFlow()

    var currentSongName = MutableStateFlow("")

    private var _toastMessage = MutableLiveData<String>()
    var toastMessage: LiveData<String> = _toastMessage

    private var _searchUrl = MutableStateFlow("")
    var searchUrl = _searchUrl.asStateFlow()

    private var _spotifyToken = MutableStateFlow("")

    lateinit var workManager: WorkManager

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            manageSpotifyResponse(cifraUseCase.getCurrentlyPlaying(_spotifyToken.value))
        }
    }

    private fun manageSpotifyResponse(response: Response<SpotifyJson>) {
        val responseSuccessful = response.isSuccessful && response.body() != null
        if (responseSuccessful) {
            val searchString = cifraUseCase.createSearchString(response.body())
            val currentSongData = cifraUseCase.createCurrentSongData(searchString, response.body())
            currentSongName.value = currentSongData.searchString!!
            setSongRefreshCycle(currentSongData)
        } else {
            val error = cifraUseCase.handleSpotifyError(
                response.code(), response.errorBody()
            )
            _toastMessage.postValue(error)
        }
    }

    private fun setSongRefreshCycle(args: CurrentSongModel) {
        //val workId = createRefreshSchedule(getRemainingTime(args.durationMs, args.progressMs))
        //setObservers(workId)
        getSongUrl(args.searchString!!)
    }

    /*private fun getRemainingTime(durationMs: Long, progressMs: Long): Long =
        (durationMs - progressMs) + 5000L

    private fun setObservers(workId: UUID) {
        workManager.getWorkInfoByIdLiveData(workId).observe(viewLifecycleOwner) {
            if (it.state.isFinished) {
                viewModel.getCurrentlyPlaying()
            }
        }
    }

    private fun createRefreshSchedule(refreshTime: Long): UUID {
        val wakeUpSchedule = OneTimeWorkRequestBuilder<CifraScheduler>()
            .setInitialDelay(refreshTime, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(wakeUpSchedule)
        return wakeUpSchedule.id
    }*/

    fun setSpotifyToken(accessToken: String) {
        _spotifyToken.value = accessToken
    }

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
                if (_currentStage.value == 3) {
                    nextConversationStage()
                }
            }
        } else {
            _toastMessage.postValue(googleResponse.message())
            Log.e(javaClass.simpleName, googleResponse.errorBody().toString())
        }
    }

    fun nextConversationStage() = _currentStage.value++

    fun backConversationStage() = _currentStage.value--

    fun restartConversationStage() { _currentStage.value = 1 }
}
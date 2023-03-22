package br.gohan.cifrafinder.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import br.gohan.cifrafinder.domain.model.CurrentSongModel
import br.gohan.cifrafinder.domain.usecase.FetchGoogleService
import br.gohan.cifrafinder.domain.usecase.FetchSpotifyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicFetchViewModel(
    private val fetchSpotifyService: FetchSpotifyService,
    private val fetchGoogleService: FetchGoogleService
) : ViewModel() {

    private var _navigationActions = MutableLiveData<NavigationActions>()
    var navigationActions: LiveData<NavigationActions> = _navigationActions

    private var _currentStage = MutableStateFlow(1)
    var currentStage = _currentStage.asStateFlow()

    private var _currentSongName = MutableStateFlow("")
    var currentSongName = _currentSongName.asStateFlow()

    private var _searchUrl = MutableStateFlow("")
    var searchUrl = _searchUrl.asStateFlow()

    private var _spotifyToken = MutableStateFlow("")
    var spotifyToken = _spotifyToken.asStateFlow()

    var songModel = MutableLiveData<CurrentSongModel?>()

    lateinit var workManager: WorkManager

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            if (_spotifyToken.value.isBlank()) {
                return@launch
            }
            val songData = withContext(Dispatchers.Default) {
                fetchSpotifyService.invoke(_spotifyToken.value)
            }
            if (songData == null) {
                postAction(NavigationActions.ToastMessage)
                return@launch
            }
            _currentSongName.value = songData.songAndArtist
            songModel.postValue(songData)

            val query = withContext(Dispatchers.Default) {
                fetchGoogleService.invoke(songData.songAndArtist)
            }

            if (query != null) {
                _searchUrl.value = query
                if (currentStage.value < 4) {
                    nextConversationStage()
                }
            }
        }
    }

    fun postAction(action: NavigationActions) {
        _navigationActions.postValue(action)
    }

    fun setSpotifyToken(accessToken: String) {
        _spotifyToken.value = accessToken
    }

    fun nextConversationStage() = _currentStage.value++

    fun setConversationStage(stage: Int) {
        _currentStage.value = stage
    }
}
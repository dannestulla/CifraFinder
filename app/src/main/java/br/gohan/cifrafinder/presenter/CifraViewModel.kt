package br.gohan.cifrafinder.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.ScreenState
import br.gohan.cifrafinder.presenter.model.SnackBarMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class CifraViewModel(
    private val spotifyService: SpotifyService,
    private val googleService: GoogleService,
) : ViewModel() {

    private var _screenState = MutableStateFlow(ScreenState())
    var screenState = _screenState.asStateFlow()

    private var _dataState = MutableStateFlow(DataState())
    var dataState = _dataState.asStateFlow()

    private var _events = MutableSharedFlow<Events>(extraBufferCapacity = 1)
    var events = _events.asSharedFlow()

    private var musicFetchJob: Job? = null

    private val shouldFetch = musicFetchJob == null || musicFetchJob?.isActive == false

    fun startMusicFetch() {
        if (shouldFetch) {
            musicFetchJob = viewModelScope.launch {
                val screenState = _screenState.value
                val dataState = _dataState.value
                val songData = getCurrentPlaying(dataState)

                if (songData == null) {
                    update(Events.ShowSnackbar(R.string.toast_no_song_being_played))
                    return@launch
                }
                if (songData.songName == screenState.songName) {
                    update(Events.WebScreen)
                    return@launch
                }
                update(dataState.copy(songData = songData))

                val tablatureLink = getTablatureLink(songData.songName)

                if (tablatureLink != null) {
                    update(
                        screenState.copy(
                            searchUrl = tablatureLink,
                            songName = songData.songName

                        )
                    )
                    update(Events.WebScreen)
                } else {
                    update(SnackBarMessage(R.string.toast_google_search_error))
                }
            }
        }
    }

    private suspend fun getCurrentPlaying(dataState: DataState) = withContext(Dispatchers.Default) {
        val songData = async {
            spotifyService.invoke(dataState.spotifyToken)
        }
        return@withContext songData
    }.await()

    private suspend fun getTablatureLink(songName: String) = withContext(Dispatchers.Default) {
        val query = async {
            googleService.invoke(songName)
        }
        return@withContext query
    }.await()


    fun <T> update(state: T) {
        viewModelScope.launch {
            when (state) {
                is DataState -> {
                    _dataState.value = state
                }
                is ScreenState -> {
                    _screenState.value = state
                }
                is Events -> {
                    _events.emit(state)
                }
            }
        }
    }
}
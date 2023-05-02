package br.gohan.cifrafinder.presenter

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DATA_STATE
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.SCREEN_STATE
import br.gohan.cifrafinder.presenter.model.ScreenState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

class CifraViewModel(
    private val spotifyService: SpotifyService,
    private val googleService: GoogleService,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    var screenState: StateFlow<ScreenState> = savedState.getStateFlow(SCREEN_STATE, ScreenState())

    var dataState: StateFlow<DataState> = savedState.getStateFlow(DATA_STATE, DataState())

    lateinit var snackbarState : SnackbarHostState

    lateinit var snackbarScope : CoroutineScope

    private var _events = MutableSharedFlow<Events>(extraBufferCapacity = 1)
    var events = _events.asSharedFlow()

    private var musicFetchJob: Job? = null

    private val shouldFetch = musicFetchJob == null || musicFetchJob?.isActive == false

    fun startMusicFetch() {
        if (shouldFetch) {
            musicFetchJob = viewModelScope.launch {
                update(screenState.value.copy(loading = true))
                val songData = getCurrentPlaying(dataState.value)

                if (songData == null) {
                    update(Events.ShowSnackbar(R.string.toast_no_song_being_played))
                    return@launch
                }
                if (songData.songName == screenState.value.songName) {
                    update(Events.WebScreen)
                    update(
                        Events.ShowSnackbar(
                            R.string.searching_for,
                            songData.songName
                        )
                    )
                    return@launch
                }
                update(dataState.value.copy(songData = songData))

                val tablatureLink = getTablatureLink(songData.songName)

                if (tablatureLink != null) {
                    update(
                        screenState.value.copy(
                            searchUrl = tablatureLink,
                            songName = songData.songName

                        )
                    )
                    update(Events.WebScreen)
                    update(
                        Events.ShowSnackbar(
                            R.string.searching_for,
                            songData.songName
                        )
                    )
                } else {
                    update(Events.ShowSnackbar(R.string.toast_google_search_error))
                }
            }
            musicFetchJob?.invokeOnCompletion {
                update(screenState.value.copy(loading = false))
            }
        }
    }

    suspend fun getCurrentPlaying(dataState: DataState) = withContext(Dispatchers.Default) {
        val songData = async {
            spotifyService.invoke(dataState.spotifyToken)
        }
        return@withContext songData
    }.await()

    suspend fun getTablatureLink(songName: String) = withContext(Dispatchers.Default) {
        val query = async {
            googleService.invoke(songName)
        }
        return@withContext query
    }.await()


    fun <T> update(state: T) {
        viewModelScope.launch {
            when (state) {
                is DataState -> {
                    savedState[DATA_STATE] = state
                }
                is ScreenState -> {
                    savedState[SCREEN_STATE] = state
                }
                is Events -> {
                    _events.emit(state)
                }
            }
        }
    }
}
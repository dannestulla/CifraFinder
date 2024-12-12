package br.gohan.cifrafinder.presenter

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DATA_STATE
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.SongData
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.SCREEN_STATE
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val spotifyService: SpotifyService,
    private val googleService: GoogleService,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    var whatIsPlayingState: StateFlow<WhatIsPlayingState> =
        savedState.getStateFlow(SCREEN_STATE, WhatIsPlayingState())

    var dataState: StateFlow<DataState> = savedState.getStateFlow(DATA_STATE, DataState())

    lateinit var snackbarState: SnackbarHostState
    lateinit var snackbarScope: CoroutineScope

    private var _events = MutableSharedFlow<AppEvents>()
    var events = _events.asSharedFlow()

    private var _currentScreen = MutableStateFlow(LOGIN)
    var currentScreen = _currentScreen.asStateFlow()

    private var musicFetchJob: Job? = null

    private val shouldFetch = musicFetchJob == null || musicFetchJob?.isActive == false

    fun startMusicFetch() {
        if (shouldFetch) {
            musicFetchJob = viewModelScope.launch {
                update(whatIsPlayingState.value.copy(loading = true))
                val song = getCurrentPlaying(dataState.value).let { songData ->
                    update(dataState.value.copy(songData = songData))
                    songData
                }

                getTablatureLink(song.name).let { link ->
                    update(
                        whatIsPlayingState.value.copy(
                            searchUrl = link,
                            songName = song.name,
                            musicDurationInSeconds = song.durationMs?.toInt()
                        )
                    )
                    _currentScreen.emit(TABLATURE_WEB)
                    update(
                        AppEvents.ShowSnackbar(
                            R.string.searching_for,
                            song.name
                        )
                    )
                }
            }
        }
        musicFetchJob?.invokeOnCompletion {
            update(whatIsPlayingState.value.copy(loading = false))
        }
    }

    suspend fun getCurrentPlaying(dataState: DataState): SongData {
        return spotifyService.invoke(dataState.spotifyToken).fold(
            onSuccess = { data ->
                data
            },
            onFailure = {
                update(AppEvents.ShowSnackbar(R.string.toast_no_song_being_played, it.message))
                throw CancellationException("Erro ao buscar a música atual")
            }
        )
    }

    suspend fun getTablatureLink(songName: String): String =
        googleService.invoke(songName).fold(
            onSuccess = { data ->
                data
            },
            onFailure = {
                update(AppEvents.ShowSnackbar(R.string.toast_google_search_error, it.message))
                throw CancellationException("Erro ao buscar a tablatura atual")
            }
        )

    fun <T> update(state: T) {
        viewModelScope.launch {
            when (state) {
                is DataState -> {
                    savedState[DATA_STATE] = state
                }

                is WhatIsPlayingState -> {
                    savedState[SCREEN_STATE] = state
                }

                is AppEvents -> {
                    _events.emit(state)
                }
            }
        }
    }

    fun handleLoginResponse(token: String?, userIsLogged: (Boolean) -> Unit) {
        if (!token.isNullOrEmpty()) {
            update(dataState.value.copy(spotifyToken = token))
            userIsLogged(true)
        } else {
            userIsLogged(false)
            update(AppEvents.ShowSnackbar(R.string.toast_login_error))
        }
    }

    fun updateScreen(screen: String) {
        _currentScreen.update { screen }
    }

    fun triggerEvent(event: AppEvents) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}
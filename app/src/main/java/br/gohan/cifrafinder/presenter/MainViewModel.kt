package br.gohan.cifrafinder.presenter

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.cifrafinder.domain.model.DATA_STATE
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.SongData
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import br.gohan.cifrafinder.presenter.model.SCREEN_STATE
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    var currentScreen = MutableStateFlow(LOGIN)

    fun startMusicFetch() {
        viewModelScope.launch {
            update(whatIsPlayingState.value.copy(loading = true))
            val song = getCurrentPlaying(dataState.value).let { songData ->
                update(dataState.value.copy(
                    songData = songData))
                update(whatIsPlayingState.value.copy(
                    songName = songData.name,
                    artCover = songData.songImage
                ))
                songData
            }
            getTablatureLink(song.name).let { url ->
                update(
                    whatIsPlayingState.value.copy(
                        searchUrl = url,
                        songName = song.name,
                        musicDurationInMiliSec = song.duration?.toInt(),
                    )
                )
                currentScreen.emit(TABLATURE_WEB)
                /*update(
                    AppEvents.ShowSnackbar(
                        R.string.searching_for,
                        song.name
                    )
                )*/
            }
            update(whatIsPlayingState.value.copy(loading = false))
        }
    }

    suspend fun getCurrentPlaying(dataState: DataState): SongData {
        return spotifyService.invoke(dataState.spotifyToken).fold(
            onSuccess = { data ->
                data
            },
            onFailure = {
                update(AppEvents.ShowSnackbar(it.message))
                update(whatIsPlayingState.value.copy(
                    loading = false,
                    songName = null
                ))
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
                update(AppEvents.ShowSnackbar(it.message))
                update(whatIsPlayingState.value.copy(loading = false))
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

    fun updateScreen(screen: String) {
        currentScreen.update { screen }
    }

    fun triggerEvent(event: AppEvents) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}
package br.gohan.cifrafinder.presenter

import android.util.Log
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

    var currentScreen = MutableStateFlow(WELCOME)

    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun startMusicFetch() {
        Log.d(TAG, "Starting music fetch")
        crashlytics.log("ViewModel: Starting music fetch")
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
            Log.d(TAG, "Got song: ${song.name}, fetching tablature")
            crashlytics.log("ViewModel: Got song, fetching tablature")
            getTablatureLink(song.name).let { url ->
                Log.d(TAG, "Got tablature URL: $url")
                update(
                    whatIsPlayingState.value.copy(
                        searchUrl = url,
                        songName = song.name,
                        musicDurationInMiliSec = song.duration?.toInt(),
                    )
                )
                currentScreen.emit(TABLATURE_WEB)
            }
            update(whatIsPlayingState.value.copy(loading = false))
        }
    }

    suspend fun getCurrentPlaying(dataState: DataState): SongData {
        Log.d(TAG, "Invoking Spotify service")
        return spotifyService.invoke(dataState.spotifyToken).fold(
            onSuccess = { data ->
                Log.d(TAG, "Spotify service success: ${data.name}")
                data
            },
            onFailure = { error ->
                Log.e(TAG, "Spotify service failed", error)
                crashlytics.log("ViewModel: Spotify service failed - ${error.message}")
                crashlytics.recordException(error)
                update(AppEvents.ShowSnackbar(error.message))
                update(whatIsPlayingState.value.copy(
                    loading = false,
                    songName = null
                ))
                throw CancellationException("Erro ao buscar a música atual")
            }
        )
    }

    suspend fun getTablatureLink(songName: String): String {
        Log.d(TAG, "Invoking Google service for: $songName")
        return googleService.invoke(songName).fold(
            onSuccess = { data ->
                Log.d(TAG, "Google service success: $data")
                data
            },
            onFailure = { error ->
                Log.e(TAG, "Google service failed", error)
                crashlytics.log("ViewModel: Google service failed - ${error.message}")
                crashlytics.recordException(error)
                update(AppEvents.ShowSnackbar(error.message))
                update(whatIsPlayingState.value.copy(loading = false))
                throw CancellationException("Erro ao buscar a tablatura atual")
            }
        )
    }

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

    companion object {
        private const val TAG = "MainViewModel"
    }
}
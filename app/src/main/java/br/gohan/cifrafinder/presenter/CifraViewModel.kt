package br.gohan.cifrafinder.presenter

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.*
import androidx.work.WorkManager
import br.gohan.cifrafinder.CifraConstants.CIFRADEBUG
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.model.ScreenState
import br.gohan.cifrafinder.domain.usecase.GoogleService
import br.gohan.cifrafinder.domain.usecase.SpotifyService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CifraViewModel(
    private val spotifyService: SpotifyService,
    private val googleService: GoogleService,
    private val app: Application,
    private val workManager: WorkManager
) : AndroidViewModel(app) {

    private var _screenState = MutableStateFlow(ScreenState())
    var screenState = _screenState.asStateFlow()

    private var _dataState = MutableStateFlow(DataState())

    private var _events = MutableSharedFlow<CifraEvents>(replay = 1)
    var events = _events.asSharedFlow()

    private var musicFetchJob: Job? = null

    lateinit var snackbarHost: SnackbarHostState

    lateinit var snackbarScope: CoroutineScope

    private val shouldFetch = musicFetchJob == null || musicFetchJob?.isActive == false

    fun startMusicFetch() {
        if (shouldFetch) {
            musicFetchJob = viewModelScope.launch {
                val screenState = _screenState.value
                val dataState = _dataState.value
                val songData = spotifyService.getCurrentPlaying(dataState)

                if (!spotifyService.isSongDataValid(songData, musicFetchJob)) {
                    showSnackbar(R.string.toast_no_song_being_played)
                    return@launch
                } else {
                    updateState(dataState.copy(songData = songData))
                }

                val tablatureLink = songData?.let { googleService.getTablatureLink(it.songName) }

                if (tablatureLink != null) {
                    Log.d(CIFRADEBUG, "viewModel chegou aqui")
                    showSnackbar(
                        R.string.searching_for,
                        songData.songName
                    )
                    updateState(screenState.copy(
                        searchUrl = tablatureLink,
                        songName = songData.songName

                    ))
                    updateState(CifraEvents.WebScreen)
                } else {
                    showSnackbar(R.string.toast_google_search_error)
                }
            }
        }
    }

    fun <T> updateState(state: T) {
        viewModelScope.launch {
            when (state) {
                is DataState -> {
                    _dataState.value = state
                }
                is ScreenState -> {
                    _screenState.value = state
                }
                is CifraEvents -> {
                    _events.emit(state)
                }
            }
        }
    }

    fun showSnackbar(id: Int, extension: String? = null) {
        snackbarScope.launch {
            snackbarHost.showSnackbar(
                app.resources.getString(id, extension)
            )
        }
    }
}
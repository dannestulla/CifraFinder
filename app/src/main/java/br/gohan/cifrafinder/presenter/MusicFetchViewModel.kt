package br.gohan.cifrafinder.presenter

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.work.WorkManager
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.usecase.FetchGoogleService
import br.gohan.cifrafinder.domain.usecase.FetchSpotifyService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MusicFetchViewModel(
    private val fetchSpotifyService: FetchSpotifyService,
    private val fetchGoogleService: FetchGoogleService,
    private val app: Application
) : AndroidViewModel(app) {

    private var _userDataState = MutableStateFlow(UserDataState())
    var userDataState = _userDataState.asStateFlow()

    private var getCurrentPlayingJob : Job? = null

    lateinit var workManager: WorkManager

    fun getCurrentlyPlaying() {
        if (getCurrentPlayingJob == null || getCurrentPlayingJob?.isActive == false) {
            getCurrentPlayingJob = viewModelScope.launch {
                val userDataState = _userDataState.value
                val songData = async {
                    fetchSpotifyService.invoke(userDataState.spotifyToken)
                }
                val songAndArtist = songData.await()?.songAndArtist
                if (songAndArtist == null) {
                    createToast(R.string.toast_no_song_being_played)
                    return@launch
                }
                if (songAndArtist != _userDataState.value.currentSongName) {
                    _userDataState.value = userDataState.copy(currentSongName = songAndArtist)
                }
                val query = async {
                    fetchGoogleService.invoke(songAndArtist)
                }
                val queryResult = query.await()
                if (queryResult != null) {
                    _userDataState.value = userDataState.copy(searchUrl = queryResult)
                } else {
                    createToast(R.string.toast_google_search_error)
                }
            }
        }
    }

    fun postAction(action: NavigationActions) {
        _userDataState.value = _userDataState.value.copy(navigationActions = action)
    }

    fun setSpotifyToken(accessToken: String) {
        _userDataState.value = userDataState.value.copy(spotifyToken = accessToken)
    }

    fun createToast(id : Int, extension: String? = null) {
            Toast.makeText(
                app,
                app.resources.getString(id, extension),
                Toast.LENGTH_LONG
            ).show()
    }
}
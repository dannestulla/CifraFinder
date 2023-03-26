package br.gohan.cifrafinder.presenter

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.work.WorkManager
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.usecase.FetchGoogleService
import br.gohan.cifrafinder.domain.usecase.FetchSpotifyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicFetchViewModel(
    private val fetchSpotifyService: FetchSpotifyService,
    private val fetchGoogleService: FetchGoogleService,
    private val app: Application
) : AndroidViewModel(app) {

    private var _userDataState = MutableStateFlow(UserDataState())
    var userDataState = _userDataState.asStateFlow()

    lateinit var workManager: WorkManager

    fun getCurrentlyPlaying() {
        viewModelScope.launch {
            val userDataState = _userDataState.value
            if (userDataState.spotifyToken.isBlank()) {
                return@launch
            }
            println("0")
            val songData = withContext(Dispatchers.Default) {
                fetchSpotifyService.invoke(userDataState.spotifyToken)
            }
            println("1")
            if (songData == null) {
                createToast(R.string.toast_no_song_being_played)
                return@launch
            }
            _userDataState.value = userDataState.copy(currentSongName = songData.songAndArtist)
            println("2")
            val query = withContext(Dispatchers.Default) {
                fetchGoogleService.invoke(songData.songAndArtist)
            }
            println("3")
            if (query != null) {
                _userDataState.value = userDataState.copy(searchUrl = query)
                postAction(NavigationActions.LastStep)
            }
        }
    }

    fun getString(id: Int): String {
        return app.resources.getString(id)
    }

    fun postAction(action: NavigationActions) {
        viewModelScope.launch {
            _userDataState.value = _userDataState.value.copy(navigationActions = action)
        }
    }

    fun setSpotifyToken(accessToken: String) {
        _userDataState.value = userDataState.value.copy(spotifyToken = accessToken)
    }

    fun createToast(message : Int) {
            Toast.makeText(
                app,
                getString(message),
                Toast.LENGTH_LONG
            ).show()
    }
}
package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import br.gohan.cifrafinder.CifraConstants.LOGGEDIN
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.helpers.SpotifyLogin
import br.gohan.cifrafinder.presenter.model.SnackBarMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CifraActivity : ComponentActivity(), KoinComponent {
    private val viewModel: CifraViewModel by viewModel()
    private lateinit var spotifyLogin: SpotifyLogin
    private val sharedPreferences: SharedPreferences by inject()

    private var userIsLogged: Boolean
        get() = sharedPreferences.getBoolean(LOGGEDIN, false)
        set(logged) = sharedPreferences.edit().putBoolean(LOGGEDIN, logged).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        spotifyLogin = SpotifyLogin(this)
        setContent {
            CifraAppContent(viewModel, userIsLogged) {
                observeEvents(it)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        spotifyLogin.handleLoginResponse(requestCode, resultCode, intent) { accessToken ->
            with(viewModel) {
                if (!accessToken.isNullOrEmpty()) {
                    update(dataState.value.copy(spotifyToken = accessToken))
                    update(Events.ThirdScreen)
                    userIsLogged = true
                } else {
                    userIsLogged = false
                    update(SnackBarMessage(R.string.toast_login_error))
                    update(Events.FirstScreen)
                }
            }
        }
    }

    private fun observeEvents(navController: NavHostController) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collectLatest {
                    when (it) {
                        is Events.FirstScreen -> {
                            navController.navigate(FIRST_SCREEN)
                        }
                        is Events.SecondScreen -> {
                            navController.navigate(SECOND_SCREEN)
                        }
                        is Events.ThirdScreen -> {
                            navController.navigate(THIRD_SCREEN)
                        }
                        is Events.WebScreen -> {
                            navController.navigate(LAST_SCREEN)
                        }
                        is Events.LogOff -> {
                            spotifyLogin.logOff()
                            userIsLogged = false
                            navController.navigate(FIRST_SCREEN)
                        }
                        is Events.MusicFetch -> {
                            viewModel.startMusicFetch()
                        }
                        is Events.Settings -> {
                            navController.navigate(SETTINGS)
                        }
                        is Events.ShowSnackbar -> {
                            showSnackbar(it.id, it.extension)
                        }
                        is Events.SpotifyLogin -> {
                            spotifyLogin.logIn()
                        }
                    }
                }
            }
        }
    }

    private suspend fun showSnackbar(id: Int, extension: String?) {
        viewModel.snackbarScope.launch {
            if (viewModel.snackbarState.currentSnackbarData == null) {
                viewModel.snackbarState.showSnackbar(
                    getString(id, extension)
                )
            }
        }
    }
}


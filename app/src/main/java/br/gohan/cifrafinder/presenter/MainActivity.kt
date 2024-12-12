package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.gohan.cifrafinder.Constants.LOGGEDIN
import br.gohan.cifrafinder.presenter.helpers.SpotifyLoginHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var spotifyLoginHelper: SpotifyLoginHelper
    private val sharedPreferences: SharedPreferences by inject()

    private var userLoggedBefore: Boolean
        get() = sharedPreferences.getBoolean(LOGGEDIN, false)
        set(logged) = sharedPreferences.edit().putBoolean(LOGGEDIN, logged).apply()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        spotifyLoginHelper = SpotifyLoginHelper(this)
        if (userLoggedBefore) {
            spotifyLoginHelper.logIn()
        }
        setContent {
            AppContent(viewModel) {
                viewModel.triggerEvent(it)
            }
        }
        observeEvents()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        spotifyLoginHelper.handleLoginResponse(requestCode, resultCode, data) { token ->
            viewModel.handleLoginResponse(token) { isLogged ->
                userLoggedBefore = isLogged
                viewModel.updateScreen(WHAT_IS_PLAYING)
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collectLatest {
                    when (it) {
                        is AppEvents.LogOff -> {
                            spotifyLoginHelper.logOff()
                            userLoggedBefore = false
                            viewModel.updateScreen(LOGIN)
                        }

                        is AppEvents.MusicFetch -> {
                            viewModel.startMusicFetch()
                        }

                        is AppEvents.Settings -> {
                            //navController.navigate(SETTINGS)
                        }

                        is AppEvents.ShowSnackbar -> {
                            showSnackbar(it.id, it.extension)
                        }

                        is AppEvents.SpotifyLogin -> {
                            spotifyLoginHelper.logIn()
                        }

                        AppEvents.BackScreen -> {
                            viewModel.updateScreen(WHAT_IS_PLAYING)
                        }

                        AppEvents.Initial -> {}
                    }
                }
            }
        }
    }

    private fun showSnackbar(id: Int, extension: String?) {
        viewModel.snackbarScope.launch {
            if (viewModel.snackbarState.currentSnackbarData == null) {
                viewModel.snackbarState.showSnackbar(
                    getString(id, extension)
                )
            }
        }
    }
}


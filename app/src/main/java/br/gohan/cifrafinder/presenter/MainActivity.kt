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
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.helpers.SpotifyLoginHelper
import br.gohan.cifrafinder.presenter.helpers.SpotifyLoginResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.core.content.edit
import android.widget.Toast

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var spotifyLoginHelper: SpotifyLoginHelper
    private val sharedPreferences: SharedPreferences by inject()
    private val crashlytics = FirebaseCrashlytics.getInstance()

    private var userLoggedBefore: Boolean
        get() = sharedPreferences.getBoolean(LOGGEDIN, false)
        set(logged) = sharedPreferences.edit { putBoolean(LOGGEDIN, logged) }


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
        spotifyLoginHelper.handleLoginResponse(requestCode, resultCode, data) { result ->
            when (result) {
                is SpotifyLoginResult.Success -> {
                    viewModel.update(viewModel.dataState.value.copy(spotifyToken = result.token))
                    userLoggedBefore = true
                    viewModel.updateScreen(WHAT_IS_PLAYING)
                }
                is SpotifyLoginResult.Error -> {
                    userLoggedBefore = false
                    crashlytics.recordException(
                        SpotifyLoginException("Spotify login failed: ${result.error}, type: ${result.type}, resultCode: ${result.resultCode}")
                    )
                    // Show detailed error in toast for debugging
                    Toast.makeText(this, "Spotify error: ${result.error} (${result.type})", Toast.LENGTH_LONG).show()
                    viewModel.update(AppEvents.ShowSnackbar(getString(R.string.toast_login_error)))
                }
            }
        }
    }

    private class SpotifyLoginException(message: String) : Exception(message)

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collectLatest { event ->
                    when (event) {
                        is AppEvents.LogOff -> {
                            spotifyLoginHelper.logOff()
                            userLoggedBefore = false
                            viewModel.updateScreen(WELCOME)
                        }

                        is AppEvents.MusicFetch -> {
                            viewModel.startMusicFetch()
                        }

                        is AppEvents.Settings -> {
                            //navController.navigate(SETTINGS)
                        }

                        is AppEvents.ShowSnackbar -> {
                            showSnackbar(event.message)
                        }

                        is AppEvents.SpotifyLogin -> {
                            spotifyLoginHelper.logIn()
                        }

                        AppEvents.BackScreen -> {
                            viewModel.updateScreen(POP_BACK_STACK)
                        }

                        AppEvents.OpenTablature -> {
                            viewModel.updateScreen(TABLATURE_WEB)
                        }

                        AppEvents.Initial -> {}
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String?) {
        viewModel.snackbarScope.launch {
            if (viewModel.snackbarState.currentSnackbarData == null && message.isNullOrEmpty().not()) {
                viewModel.snackbarState.showSnackbar(message!!)
            }
        }
    }
}


package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.gohan.cifrafinder.CifraConstants.LOGGEDIN
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.domain.usecase.SpotifyLogin
import br.gohan.cifrafinder.presenter.components.ui.theme.CifraFinderTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val viewModel: CifraViewModel by viewModel()
    private lateinit var spotifyLogin: SpotifyLogin
    private val sharedPreferences: SharedPreferences by inject()

    private var userIsLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(LOGGEDIN, false)
        set(logged) = sharedPreferences.edit().putBoolean(LOGGEDIN, logged).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spotifyLogin = SpotifyLogin(this)
        setContent {
            CifraFinderTheme {
                val navController = rememberNavController()
                observeEvents(navController)
                viewModel.snackbarScope = rememberCoroutineScope()
                viewModel.snackbarHost = remember { SnackbarHostState() }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostCifra(
                        viewModel,
                        navController,
                        viewModel.snackbarHost,
                    ) {
                        viewModel.updateState(it)
                    }
                }
            }
        }
        if (userIsLoggedIn) {
            viewModel.updateState(CifraEvents.SecondScreen)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        spotifyLogin.handleLoginResponse(requestCode, resultCode, intent) { accessToken ->
            with(viewModel) {
                if (!accessToken.isNullOrEmpty()) {
                    updateState(
                        DataState(spotifyToken = accessToken)
                    )
                    updateState(CifraEvents.ThirdScreen)
                    userIsLoggedIn = true
                } else {
                    updateState(CifraEvents.FirstScreen)
                    showSnackbar(R.string.toast_login_error)
                }
            }
        }
    }

    private fun observeEvents(navController: NavHostController) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collectLatest {
                    when (it) {
                        is CifraEvents.FirstScreen -> {
                            navController.navigate(FIRST_SCREEN)
                        }
                        is CifraEvents.SecondScreen -> {
                            navController.navigate(SECOND_SCREEN)
                            spotifyLogin.logInSpotify()
                        }
                        is CifraEvents.ThirdScreen -> {
                            navController.navigate(THIRD_SCREEN)
                        }
                        is CifraEvents.WebScreen -> {
                            navController.navigate(LAST_SCREEN)
                        }
                        is CifraEvents.LogOff -> {
                            userIsLoggedIn = false
                            navController.navigate(FIRST_SCREEN)
                        }
                        is CifraEvents.StartMusicFetch -> {
                            viewModel.startMusicFetch()
                        }
                        is CifraEvents.Settings -> {
                            navController.navigate(SETTINGS)
                        }
                    }
                }
            }
        }
    }
}

package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.screens.*
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CifraViewModel by viewModel()
    private lateinit var spotifyLoginHelper: SpotifyLoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spotifyLoginHelper = SpotifyLoginHelper(this)
        viewModel.workManager = WorkManager.getInstance(this)
        setContent {
            CifraFinderTheme {
                val navController = rememberNavController()
                observeNavigationActions(navController)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostCifra(navController, viewModel)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        spotifyLoginHelper.handleLoginResponse(requestCode, resultCode, intent) { accessToken ->
            if (!accessToken.isNullOrEmpty()) {
                viewModel.setSpotifyToken(accessToken)
            } else {
                viewModel.postAction(NavigationActions.FirstStep)
                viewModel.createToast(R.string.toast_login_error)
            }
        }
    }

    private fun observeNavigationActions(navController: NavHostController) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userDataState.collect {
                    when (it.navigationActions) {
                        // Asks for user to log in Spotify
                        is NavigationActions.FirstStep -> {
                            navController.navigate(FIRST_STEP)
                        }
                        // Shows loading screen and opens Spotify Log in
                        is NavigationActions.SecondStep -> {
                            navController.navigate(SECOND_STEP)
                            spotifyLoginHelper.logInSpotify()
                        }
                        // Asks for user to play a song and shows get playing song button
                        is NavigationActions.ThirdStep -> {
                            navController.navigate(THIRD_STEP)
                        }
                        // Open Webview with the tablature
                        is NavigationActions.LastStep -> {
                            navController.navigate(LAST_STEP)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

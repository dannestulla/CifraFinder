package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import br.gohan.cifrafinder.presenter.screens.ConversationScreen
import br.gohan.cifrafinder.presenter.screens.WebScreen
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import com.spotify.sdk.android.auth.AuthorizationClient
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MusicFetchViewModel by viewModel()
    private lateinit var spotifyLoginHelper: SpotifyLoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spotifyLoginHelper = SpotifyLoginHelper(this)
        viewModel.workManager = WorkManager.getInstance(this)
        spotifyLoginHelper.logInSpotify()
        setContent {
            CifraFinderTheme {
                val navController = rememberNavController()
                observeActions(navController)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "conversation"
                    ) {
                        composable(route = "conversation") {
                            ConversationScreen(viewModel)
                        }
                        composable(route = "webview") {
                            WebScreen(viewModel, navController)
                        }
                    }
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
                viewModel.setConversationStage(3)
            }
        }
    }

    private fun observeActions(navController: NavHostController) {
        viewModel.navigationActions.observe(this){
            when (it) {
                is NavigationActions.WebView -> {
                    navController.navigate("webview")
                }
                is NavigationActions.LogInSpotify -> spotifyLoginHelper.logInSpotify()
                is NavigationActions.LogOffSpotify -> {
                    AuthorizationClient.clearCookies(this@MainActivity)
                    viewModel.setConversationStage(1)
                }
                is NavigationActions.GetCurrentlyPlaying -> {
                    viewModel.getCurrentlyPlaying()
                }
                is NavigationActions.ToastMessage -> {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
}

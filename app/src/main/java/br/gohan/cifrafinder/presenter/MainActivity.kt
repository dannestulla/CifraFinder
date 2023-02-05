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
        toastObserver()
        setContent {
            CifraFinderTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "conversation"
                    ) {
                        composable(route = "conversation") {
                            ConversationScreen(viewModel, spotifyLoginHelper,
                                logOff = {
                                    AuthorizationClient.clearCookies(this@MainActivity)
                                    viewModel.restartConversationStage()
                            },
                                navigateToWebView = {
                                    navController.navigate("webview")
                            })
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
            viewModel.setSpotifyToken(accessToken)
            viewModel.nextConversationStage()
        }
    }

    private fun toastObserver() {
        viewModel.toastMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }
}

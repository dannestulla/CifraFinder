package br.gohan.cifrafinder.presenter

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.gohan.cifrafinder.presenter.screens.*

@Composable
fun NavHostCifra(
    viewModel: CifraViewModel,
    userIsLoggedIn: Boolean,
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    action: (Events) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (userIsLoggedIn) SECOND_SCREEN else FIRST_SCREEN
    ) {
        composable(route = FIRST_SCREEN) {
            FirstScreen(snackbarHost, action)
        }
        composable(route = SECOND_SCREEN) {
            SecondScreen(snackbarHost ,action)
        }
        composable(route = THIRD_SCREEN) {
            val thirdScreenState = viewModel.screenState.collectAsStateWithLifecycle().value
            ThirdScreen(thirdScreenState, snackbarHost, action)
        }
        composable(route = LAST_SCREEN) {
            BackHandler {
                navController.navigate(THIRD_SCREEN)
            }
            val webScreenState = viewModel.screenState.collectAsStateWithLifecycle().value
            WebScreen(webScreenState, action, snackbarHost)
        }

        composable(route = SETTINGS) {
            SettingsScreen(action)
        }
    }
}

sealed class Events {
    object FirstScreen : Events()
    object SecondScreen : Events()
    object ThirdScreen : Events()
    object WebScreen : Events()
    object LogOff : Events()
    object MusicFetch : Events()
    object Settings : Events()
    object SpotifyLogin : Events()
    data class ShowSnackbar(val id: Int, val extension: String? = null) : Events()
}

const val FIRST_SCREEN = "firstScreen"
const val SECOND_SCREEN = "secondScreen"
const val THIRD_SCREEN = "thirdScreen"
const val LAST_SCREEN = "lastScreen"
const val SETTINGS = "settings"

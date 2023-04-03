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
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    action: (CifraEvents) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = FIRST_SCREEN
    ) {
        composable(route = FIRST_SCREEN) {
            FirstScreen(action)
        }
        composable(route = SECOND_SCREEN) {
            SecondScreen(action, snackbarHost)
        }
        composable(route = THIRD_SCREEN) {
            val thirdScreenState = viewModel.screenState.collectAsStateWithLifecycle().value
            ThirdScreen(thirdScreenState, action, snackbarHost)
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

sealed class CifraEvents {

    object FirstScreen : CifraEvents()
    object SecondScreen : CifraEvents()
    object ThirdScreen : CifraEvents()
    object WebScreen : CifraEvents()
    object LogOff : CifraEvents()
    object StartMusicFetch : CifraEvents()
    object Settings: CifraEvents()
}

const val FIRST_SCREEN = "firstScreen"
const val SECOND_SCREEN = "secondScreen"
const val THIRD_SCREEN = "thirdScreen"
const val LAST_SCREEN = "lastScreen"
const val SETTINGS = "settings"

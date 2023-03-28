package br.gohan.cifrafinder.presenter

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.gohan.cifrafinder.presenter.components.FirstStepScreen
import br.gohan.cifrafinder.presenter.components.SecondStepScreen
import br.gohan.cifrafinder.presenter.components.ThirdStepScreen
import br.gohan.cifrafinder.presenter.components.WebScreen

@Composable
fun NavHostCifra(navController: NavHostController, viewModel: CifraViewModel) {
    NavHost(
        navController = navController,
        startDestination = FIRST_STEP
    ) {
        composable(route = FIRST_STEP) {
            FirstStepScreen(viewModel)
        }
        composable(route = SECOND_STEP) {
            SecondStepScreen(viewModel)
        }
        composable(route = THIRD_STEP) {
            ThirdStepScreen(viewModel)
        }
        composable(route = LAST_STEP) {
            WebScreen(viewModel, navController)
        }
    }
}

sealed class NavigationActions {

    // Asks for user to log in Spotify
    object FirstStep : NavigationActions()

    // Shows loading screen and opens Spotify Log in
    object SecondStep : NavigationActions()

    // Asks for user to play a song and shows get playing song button
    object ThirdStep : NavigationActions()

    // Open Webview with the tablature
    object LastStep : NavigationActions()
}

const val FIRST_STEP = "firstStep"
const val SECOND_STEP = "secondStep"
const val THIRD_STEP = "thirdStep"
const val LAST_STEP = "lastStep"

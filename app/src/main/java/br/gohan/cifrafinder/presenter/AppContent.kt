package br.gohan.cifrafinder.presenter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gohan.cifrafinder.presenter.screens.LoginScreen
import br.gohan.cifrafinder.presenter.screens.SettingsScreen
import br.gohan.cifrafinder.presenter.screens.TablatureWebScreen
import br.gohan.cifrafinder.presenter.screens.WhatIsPlayingScreen
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme

@Composable
fun AppContent(
    viewModel: MainViewModel,
    events: (AppEvents) -> Unit
) {
    CifraFinderTheme {
        val navController = rememberNavController()
        val snackBarHost = remember { SnackbarHostState() }
        val snackBarScope = rememberCoroutineScope()
        viewModel.snackbarState = snackBarHost
        viewModel.snackbarScope = snackBarScope

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            val screen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val playingState =
                viewModel.whatIsPlayingState.collectAsStateWithLifecycle().value

            LaunchedEffect(screen) {
                navController.navigate(screen)
            }

            NavHost(
                navController = navController,
                startDestination = screen
            ) {
                composable(route = LOGIN) {
                    LoginScreen(snackBarHost, events)
                }
                composable(route = WHAT_IS_PLAYING) {
                    WhatIsPlayingScreen(playingState, snackBarHost, events)
                }
                composable(route = TABLATURE_WEB) {
                    TablatureWebScreen(playingState, snackBarHost, events) {
                        viewModel.updateScreen(WHAT_IS_PLAYING)
                    }
                }
                composable(route = SETTINGS) {
                    SettingsScreen {

                    }
                }
            }
        }
    }
}

const val LOGIN = "LOGIN_SCREEN"
const val WHAT_IS_PLAYING = "WHAT_IS_PLAYING_SCREEN"
const val TABLATURE_WEB = "TABLATURE_WEB_SCREEN"
const val SETTINGS = "settings"

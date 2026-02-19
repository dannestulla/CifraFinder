package br.gohan.cifrafinder.presenter

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import br.gohan.cifrafinder.presenter.ui.screens.LoginScreen
import br.gohan.cifrafinder.presenter.ui.screens.SettingsScreen
import br.gohan.cifrafinder.presenter.ui.screens.TablatureWebScreen
import br.gohan.cifrafinder.presenter.ui.screens.WhatIsPlayingScreen
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme
import kotlinx.coroutines.flow.update

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
                if (screen == POP_BACK_STACK) {
                    navController.popBackStack()
                } else {
                    navController.navigate(screen)
                }
            }

            NavHost(
                navController = navController,
                startDestination = screen,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    )
                }
            ) {
                composable(route = LOGIN) {
                    LoginScreen(snackBarHost, events)
                }
                composable(route = WHAT_IS_PLAYING) {
                    WhatIsPlayingScreen(playingState, snackBarHost, events)
                }
                composable(route = TABLATURE_WEB) {
                    TablatureWebScreen(playingState, snackBarHost, events) {
                        viewModel.currentScreen.update { POP_BACK_STACK }
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
const val POP_BACK_STACK = "POP_BACK_STACK"
const val TABLATURE_WEB = "TABLATURE_WEB_SCREEN"
const val SETTINGS = "settings"

package br.gohan.cifrafinder.presenter.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.gohan.cifrafinder.domain.model.DataState
import br.gohan.cifrafinder.presenter.CifraViewModel
import br.gohan.cifrafinder.presenter.NavHostCifra
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme

@Composable
fun CifraAppCompose(
    viewModel: CifraViewModel,
    userIsLoggedIn: Boolean,
    callback : (NavHostController) -> Unit) {
    CifraFinderTheme {
        val navController = rememberNavController()
        val snackBarHost = remember { SnackbarHostState() }
        val snackBarMessage = rememberCoroutineScope()
        viewModel.update(
            DataState(
                snackBarHost = snackBarHost,
                snackBarScope = snackBarMessage
            )
        )
        Surface(
            modifier = Modifier.fillMaxSize().fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHostCifra(
                viewModel,
                userIsLoggedIn,
                navController,
                snackBarHost,
            ) {
                viewModel.update(it)
            }
        }
        callback.invoke(navController)
    }
}
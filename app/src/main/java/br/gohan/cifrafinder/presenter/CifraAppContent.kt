package br.gohan.cifrafinder.presenter

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
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme

@Composable
fun CifraAppContent(
    viewModel: CifraViewModel,
    userIsLoggedIn: Boolean,
    callback: (NavHostController) -> Unit
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
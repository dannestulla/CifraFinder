package br.gohan.cifrafinder.presenter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.ui.components.NormalButton
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme

@Composable
fun LoginScreen(
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    event: (AppEvents) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 60.dp,
                    alignment = Alignment.CenterVertically
                ),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    text = stringResource(id = R.string.first_step_title)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    text = stringResource(id = R.string.first_step_description)
                )
                NormalButton(string = R.string.first_step_button) {
                    event.invoke(AppEvents.SpotifyLogin)
                }
            }
        })
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FirstStepScreenPreview() {
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen {

            }
        }
    }
}
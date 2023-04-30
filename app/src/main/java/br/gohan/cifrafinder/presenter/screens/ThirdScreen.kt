package br.gohan.cifrafinder.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.Events
import br.gohan.cifrafinder.presenter.components.CifraFAB
import br.gohan.cifrafinder.presenter.components.FABType
import br.gohan.cifrafinder.presenter.components.LogoutDialog
import br.gohan.cifrafinder.presenter.model.ScreenState
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    screenState: ScreenState,
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    event: (Events) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        content = { padding ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        text = if (screenState.songName.isNotEmpty()) {
                            stringResource(
                                id = R.string.fourth_step_title,
                                screenState.songName
                            )
                        } else {
                            stringResource(id = R.string.third_step_title)
                        }
                    )
                    ElevatedButton(
                        enabled = screenState.loading.not(),
                        colors = ButtonDefaults.buttonColors(),
                        onClick = {
                            event.invoke(Events.MusicFetch)
                        }) {
                        Text(
                            stringResource(id = R.string.third_step_button_search_music),
                            fontSize = 20.sp
                        )
                    }
                    if (screenState.loading) {
                        CircularProgressIndicator()
                    } else {
                        Box(modifier = Modifier.size(40.dp))
                    }
                    if (openDialog.value) {
                        LogoutDialog(openDialog) {
                            event.invoke(Events.LogOff)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            CifraFAB(type = FABType.LOG_OFF) {
                openDialog.value = true
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ThirdStepPreview() {
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ThirdScreen(ScreenState("A lua - Xitãozinho e Chororó")) {}
        }
    }
}
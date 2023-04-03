package br.gohan.cifrafinder.presenter.screens

import android.util.Log
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
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.domain.model.ScreenState
import br.gohan.cifrafinder.presenter.CifraEvents
import br.gohan.cifrafinder.presenter.components.CifraFAB
import br.gohan.cifrafinder.presenter.components.FABType
import br.gohan.cifrafinder.presenter.components.LogoutDialog
import br.gohan.cifrafinder.presenter.components.ui.theme.CifraFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    screenState: ScreenState,
    event: (CifraEvents) -> Unit,
    snackbarHost: SnackbarHostState
) {
    val openDialog = remember { mutableStateOf(false) }
    Log.d(CifraConstants.CIFRADEBUG, "thirdScreen chegou aqui")
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost)},
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
                        colors = ButtonDefaults.buttonColors(),
                        onClick = {
                            event.invoke(CifraEvents.StartMusicFetch)
                        }) {
                        Text(
                            stringResource(id = R.string.third_step_button_search_music),
                            fontSize = 20.sp
                        )
                    }
                }
                if (openDialog.value) {
                    LogoutDialog(openDialog) {
                        event.invoke(CifraEvents.FirstScreen)
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
        }
    }
}
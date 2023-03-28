package br.gohan.cifrafinder.presenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.CifraViewModel
import br.gohan.cifrafinder.presenter.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdStepScreen(
    viewModel: CifraViewModel
) {
    val openDialog = remember { mutableStateOf(false) }
    val userDataState = viewModel.userDataState.collectAsStateWithLifecycle().value
    Scaffold(
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
                        text = if (userDataState.currentSongName.isNotEmpty()) {
                            stringResource(
                                id = R.string.fourth_step_title,
                                userDataState.currentSongName
                            )
                        } else {
                            stringResource(id = R.string.third_step_title)
                        }
                    )
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(),
                        onClick = {
                            viewModel.getCurrentlyPlaying()
                        }) {
                        Text(
                            stringResource(id = R.string.third_step_button_search_music),
                            fontSize = 20.sp
                        )
                    }
                }
            }
            if (openDialog.value) {
                DialogLogoff(openDialog) {
                    viewModel.postAction(NavigationActions.FirstStep)
                }
            }
        },
        floatingActionButton = {
            CifraFAB(type = FABType.LOGOFF) {
                openDialog.value = true
            }
        }
    )
}
package br.gohan.cifrafinder.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.CifraViewModel
import br.gohan.cifrafinder.presenter.NavigationActions
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThirdStepScreen(
    viewModel: CifraViewModel
) {
    val openDialog = remember { mutableStateOf(false) }
    val userDataState = viewModel.userDataState.collectAsStateWithLifecycle().value
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 60.dp,
            alignment = Alignment.CenterVertically
        ),
    ) {
        ElevatedButton(
            colors = ButtonDefaults.filledTonalButtonColors(),
            onClick = {
                if (userDataState.currentSongName.isEmpty()) {
                    viewModel.createToast(R.string.toast_no_song_being_played)
                    viewModel.getCurrentlyPlaying()
                    return@ElevatedButton
                } else if (userDataState.searchUrl.isEmpty()) {
                    viewModel.createToast(
                        R.string.fetching_query,
                    )
                    viewModel.getCurrentlyPlaying()
                    return@ElevatedButton
                } else {
                    viewModel.postAction(NavigationActions.LastStep)
                }
            }) {
            Text(
                stringResource(id = R.string.third_step_button_search_music),
                fontSize = 20.sp
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            text = if (userDataState.currentSongName.isNotEmpty()) {
                stringResource(id = R.string.fourth_step_title, userDataState.currentSongName)
            } else {
                stringResource(id = R.string.third_step_title)
            }
        )
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(),
            onClick = {
                openDialog.value = true
            }) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = stringResource(id = R.string.description_icon_check),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                fontSize = 20.sp,
                text = stringResource(id = R.string.third_step_button)
            )
        }
    }
    if (openDialog.value) {
        DialogLogoff(openDialog) {
            viewModel.postAction(NavigationActions.FirstStep)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ThirdStepScreenPreview() {
    val viewModel = koinViewModel<CifraViewModel>()
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ThirdStepScreen(
                viewModel
            )
        }
    }
}
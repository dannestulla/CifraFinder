package br.gohan.cifrafinder.presenter.screens

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
import androidx.compose.runtime.mutableStateOf
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
import br.gohan.cifrafinder.presenter.components.CifraFAB
import br.gohan.cifrafinder.presenter.components.FABType
import br.gohan.cifrafinder.presenter.components.NormalButton
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme

@Composable
fun WhatIsPlayingScreen(
    whatIsPlayingState: WhatIsPlayingState,
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    event: (AppEvents) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    100.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    text = if (whatIsPlayingState.songName.isNullOrEmpty().not()) {
                        stringResource(
                            id = R.string.fourth_step_title,
                            whatIsPlayingState.songName!!
                        )
                    } else {
                        stringResource(id = R.string.third_step_title)
                    }
                )
                NormalButton(
                    isEnabled = whatIsPlayingState.loading.not(),
                    onClick = {
                        event.invoke(AppEvents.MusicFetch)
                    },
                    string = R.string.third_step_button_search_music
                )
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
            WhatIsPlayingScreen(
                WhatIsPlayingState(
                    "A lua - Xitãozinho e Chororó",
                    musicDurationInSeconds = 0
                )
            ) {}
        }
    }
}
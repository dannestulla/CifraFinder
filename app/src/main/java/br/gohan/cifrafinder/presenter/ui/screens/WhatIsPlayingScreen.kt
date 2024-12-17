package br.gohan.cifrafinder.presenter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.ui.components.CifraFAB
import br.gohan.cifrafinder.presenter.ui.components.FABType
import br.gohan.cifrafinder.presenter.ui.components.NormalButton
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import br.gohan.cifrafinder.presenter.ui.components.MessageDialog
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme
import coil3.compose.AsyncImage

@Composable
fun WhatIsPlayingScreen(
    whatIsPlayingState: WhatIsPlayingState,
    snackbarHost: SnackbarHostState = remember { SnackbarHostState() },
    event: (AppEvents) -> Unit
) {
    var logoffDialog by remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    20.dp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                if (logoffDialog) MessageDialog(R.string.log_off_dialog_title) { logOut ->
                    logoffDialog = !logoffDialog
                    if (logOut) event(AppEvents.LogOff)
                }
                if (whatIsPlayingState.songName.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        text = stringResource(id = R.string.third_step_title)
                    )
                } else {
                    Text(modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        text = stringResource(R.string.playing))
                    AsyncImage(
                        model = whatIsPlayingState.artCover,
                        contentDescription = stringResource(R.string.album_cover) + whatIsPlayingState.songName,
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        text =
                                whatIsPlayingState.songName
                    )
                }

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
            Row(
                Modifier.fillMaxWidth().padding(start = 10.dp)
            ) {
                CifraFAB(type = FABType.LOG_OFF) {
                    logoffDialog = true
                }
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
                    musicDurationInMiliSec = 0
                )
            ) {}
        }
    }
}
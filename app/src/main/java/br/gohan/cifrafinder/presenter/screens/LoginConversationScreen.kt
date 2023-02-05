package br.gohan.cifrafinder.presenter.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.presenter.MusicFetchViewModel
import br.gohan.cifrafinder.presenter.SpotifyLoginHelper
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ConversationScreen(
    viewModel: MusicFetchViewModel,
    spotifyLoginHelper: SpotifyLoginHelper,
    navigateToWebView: () -> Unit,
    logOff: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }
    val currentStage = viewModel.currentStage.collectAsState().value
    val currentSong = viewModel.currentSongName.collectAsState().value
    var firstText = ""
    var secondText = ""
    var buttonText = ""
    when (currentStage) {
        1 -> {
            firstText = "Primeiro logue na sua conta do Spotify"
            secondText = "Assim saberemos qual música está sendo tocada para buscarmos a tablatura"
            buttonText = "Fazer login no Spotify"
        }
        2 -> {
            buttonText = "Logando..."
        }
        3 -> {
            viewModel.getCurrentlyPlaying()
            secondText = "Comece a tocar uma música no Spotify para buscar sua tablatura"
            buttonText = "Logado no Spotify"
        }
        4 -> {
            firstText = "Tocando: $currentSong"
            buttonText = "Logado no Spotify"
        }
    }
    AnimatedContent(
        targetState = currentStage,
        transitionSpec = {
            fadeIn() with fadeOut()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                text = firstText
            )
            if (currentStage == 2) {
                CircularProgressIndicator()
            }
            if (currentStage == 4) {
                ElevatedButton(
                    colors =
                    ButtonDefaults.filledTonalButtonColors(),
                    onClick = {
                        navigateToWebView.invoke()
                    }) {
                    Text("Buscar tablatura!", fontSize = 20.sp)
                }
            }
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                text = secondText
            )
            ElevatedButton(
                enabled = currentStage != 2,
                colors = if (currentStage > 2) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.filledTonalButtonColors()
                },
                onClick = {
                    handleButtonFlow(currentStage, viewModel, spotifyLoginHelper) {
                        openDialog.value = true
                    }
                }) {
                if (currentStage > 2) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = "Localized description",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    fontSize = 20.sp,
                    text = buttonText
                )
            }
        }
    }
    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false },
            text = {
                Text(
                    text = "Deseja fazer logoff do Spotify?",
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        logOff.invoke()
                    }) {
                    Text("Sim")
                }
            }, dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Não")
                }
            })
    }
    /* Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Bottom
     ) {
         Button(onClick = { viewModel.nextConversationStage() }) {
             Text("passar passo")
         }
         Button(onClick = { viewModel.backConversationStage() }) {
             Text("voltar passo")
         }
         Text("currentStage $currentStage")
     }*/
}

fun handleButtonFlow(
    currentStage: Int,
    viewModel: MusicFetchViewModel,
    spotifyLoginHelper: SpotifyLoginHelper,
    logOff: () -> Unit
) {
    return when (currentStage) {
        1 -> {
            viewModel.nextConversationStage()
            spotifyLoginHelper.logInSpotify()
        }
        else -> logOff.invoke()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConversationScreen() {
    val viewModel = koinViewModel<MusicFetchViewModel>()
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ConversationScreen(
                viewModel,
                spotifyLoginHelper = SpotifyLoginHelper(null), {}, {})
        }
    }
}
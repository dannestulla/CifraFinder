package br.gohan.cifrafinder.presenter.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.presenter.MusicFetchViewModel

@Composable
fun LoginScreen(viewModel: MusicFetchViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Text(text = "Buscar no Cifra Club!", fontSize = 30.sp)
        Button(onClick = {
            if (viewModel.spotifyToken.value?.isNotEmpty() == true) {
                viewModel.getCurrentlyPlaying()
            }
        }) {
            Text(
                text = "Buscar Música",
                fontSize = 25.sp
            )
        }
        Button(onClick = {
        }) {
            Text(text = "Login no Spotify",
                fontSize = 25.sp
            )
        }
    }
}
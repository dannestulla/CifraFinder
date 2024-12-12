package br.gohan.cifrafinder.presenter.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.components.LogoutDialog

/**
 * WORK IN PROGRESS, YET TO BE IMPLEMENTED
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    event: (AppEvents) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    /*IconButton(onClick = { event.invoke(AppEvents.WhatIsPlayingScreen) }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, "back button")
                    }*/
                },
                title = { Text("Configurações") })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                ListItem(
                    headlineContent = { Text("Busca automática") },
                    supportingContent = { Text("Atualiza o navegador assim que uma nova música tocar") },
                    trailingContent = {
                        Checkbox(checked = false, onCheckedChange = {

                        })
                    }
                )
                ListItem(
                    headlineContent = { Text("Login automático") },
                    supportingContent = { Text("Após logado pela primeira vez, entrará automaticamente") },
                    trailingContent = { Checkbox(checked = true, onCheckedChange = { }) }
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                ElevatedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { openDialog.value = true }) {
                    Text("Logoff do Spotify")
                }
            }
        })
    if (openDialog.value) {
        LogoutDialog(openDialog) {
            event.invoke(AppEvents.LogOff)
        }
    }
}

@Preview
@Composable
fun previewSettingsScreen() {
    SettingsScreen {}
}
package br.gohan.cifrafinder.presenter.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.Events
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen(
    snackbarHost: SnackbarHostState,
    action: (Events) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost)},
        content = { padding ->
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
                    text = stringResource(id = R.string.first_step_title)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    text = stringResource(id = R.string.first_step_description)
                )
                ElevatedButton(
                    colors = ButtonDefaults.buttonColors(),
                    onClick = {
                        action.invoke(Events.SecondScreen)
                    }) {
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        fontSize = 20.sp,
                        text = stringResource(id = R.string.first_step_button)
                    )
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
        }
    }
}
package br.gohan.cifrafinder.presenter.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import br.gohan.cifrafinder.presenter.CifraEvents
import br.gohan.cifrafinder.presenter.components.ui.theme.CifraFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    event: (CifraEvents) -> Unit,
    snackbarHost: SnackbarHostState
) {
    Log.d(CifraConstants.CIFRADEBUG, "secondScreen chegou aqui")

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
            CircularProgressIndicator()
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                text = stringResource(id = R.string.second_step_description)
            )
        }
    },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecondStepScreenPreview() {
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            //SecondScreen(CifraConstants.screenStateMock) {}
        }
    }
}
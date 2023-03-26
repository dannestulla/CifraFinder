package br.gohan.cifrafinder.presenter.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.MusicFetchViewModel
import br.gohan.cifrafinder.presenter.NavigationActions
import br.gohan.cifrafinder.presenter.screens.ui.theme.CifraFinderTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecondStepScreen(viewModel: MusicFetchViewModel) {
    val userDataState = viewModel.userDataState.collectAsStateWithLifecycle()
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
        if (userDataState.value.spotifyToken.isNotBlank()) {
            viewModel.postAction(NavigationActions.ThirdStep)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecondStepScreenPreview() {
    val viewModel = koinViewModel<MusicFetchViewModel>()
    CifraFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SecondStepScreen(viewModel)
        }
    }
}
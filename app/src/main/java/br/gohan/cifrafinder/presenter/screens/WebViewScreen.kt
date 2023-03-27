package br.gohan.cifrafinder.presenter.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.CifraViewModel
import br.gohan.cifrafinder.presenter.THIRD_STEP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreen(
    viewModel: CifraViewModel,
    navController: NavHostController,
    ) {
    BackHandler {
        navController.navigate(THIRD_STEP)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.getCurrentlyPlaying()
                },
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = stringResource(id = R.string.description_icon_check)
                )
            }
        }, content = { padding ->
            Surface(
                modifier = Modifier.padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                WebViewComponent(viewModel)
            }
        })
}

@Composable
fun WebViewComponent(viewModel: CifraViewModel) {
    val userDataState = viewModel.userDataState.collectAsStateWithLifecycle().value
    if (userDataState.currentSongName.isNotBlank()) {
        viewModel.createToast(R.string.searching_for, userDataState.currentSongName)
    }
    AndroidView(factory = {
        WebView(it).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(userDataState.searchUrl)
        }
    }, update = {
        //it.reload()
    })
}

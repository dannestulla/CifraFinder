package br.gohan.cifrafinder.presenter.screens

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.Events
import br.gohan.cifrafinder.presenter.components.CifraFAB
import br.gohan.cifrafinder.presenter.components.FABType
import br.gohan.cifrafinder.presenter.model.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreen(
    screenState: ScreenState,
    event: (Events) -> Unit,
    snackbarHost: SnackbarHostState,
) {
    event(Events.ShowSnackbar(
        R.string.searching_for,
        screenState.songName
    ))
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHost)},
        floatingActionButton = {
            CifraFAB(type = FABType.REFRESH) {
                event.invoke(Events.MusicFetch)
            }
        }, content = { padding ->
            Surface(
                modifier = Modifier.padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                WebViewComponent(screenState.searchUrl)
            }
        })
}

@Composable
fun WebViewComponent(searchUrl: String) {
    AndroidView(
        factory = {
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
                loadUrl(searchUrl)
            }
        })
}
package br.gohan.cifrafinder.presenter.screens

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.components.CifraFAB
import br.gohan.cifrafinder.presenter.components.FABType
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import kotlin.math.roundToInt

@Composable
fun TablatureWebScreen(
    whatIsPlayingState: WhatIsPlayingState,
    snackbarState: SnackbarHostState,
    event: (AppEvents) -> Unit,
    backScreen: () -> Unit
) {
    with(whatIsPlayingState) {
        if (searchUrl.isNullOrEmpty() ||
            songName.isNullOrEmpty() ||
            musicDurationInSeconds == null
        ) {
            event.invoke(AppEvents.BackScreen)
            return
        }
    }
    val offsetSaver = Saver<Offset, List<Float>>(
        save = { listOf(it.x, it.y) },
        restore = { Offset(it[0], it[1]) }
    )

    var fabOffset by rememberSaveable(stateSaver = offsetSaver) {
        mutableStateOf(Offset(0f, 0f))
    }
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
        floatingActionButton = {
            Column(
                Modifier
                    .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            fabOffset += dragAmount
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
            ) {
                CifraFAB(type = FABType.BACK) {
                    backScreen.invoke()
                }
                Spacer(Modifier.height(10.dp))
                CifraFAB(type = FABType.REFRESH) {
                    event.invoke(AppEvents.MusicFetch)
                }
            }

        }, content = { padding ->
            Surface(
                modifier = Modifier.padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                WebViewComponent(
                    whatIsPlayingState.searchUrl!!,
                    whatIsPlayingState.musicDurationInSeconds!!
                )
            }
        })

}

@Composable
fun WebViewComponent(searchUrl: String, musicDurationInMiliSeconds: Int) {
    var pageHeight by remember { mutableIntStateOf(0) }
    val currentScrollY by remember { mutableIntStateOf(0) }
    val animator = remember { Animatable(0f) }

    Log.e("WebViewComponent", "animator: ${animator.value}")
    AndroidView(
        factory = {
            WebView(it).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        evaluateJavascript(
                            """
                            (function() {
                                return document.body.scrollHeight;
                            })();
                            """.trimIndent()
                        ) { heightString ->
                            heightString?.toIntOrNull()?.let {
                                pageHeight = it
                            }
                        }
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                loadUrl(searchUrl)
            }
        },
        update = { webView ->
            webView.scrollTo(0, animator.value.toInt())
        }
    )

    LaunchedEffect(pageHeight) {
        if (pageHeight > 0) {
            animator.animateTo(
                targetValue = (musicDurationInMiliSeconds).toFloat(),
                animationSpec = tween(durationMillis = musicDurationInMiliSeconds)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            //animator.stop()
        }
    }
}

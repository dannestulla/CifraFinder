package br.gohan.cifrafinder.presenter.ui.screens

import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.gohan.cifrafinder.presenter.AppEvents
import br.gohan.cifrafinder.presenter.ui.components.CifraFAB
import br.gohan.cifrafinder.presenter.ui.components.FABType
import br.gohan.cifrafinder.presenter.model.WhatIsPlayingState
import br.gohan.cifrafinder.presenter.ui.components.webViewClientConfig
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TablatureWebScreen(
    whatIsPlayingState: WhatIsPlayingState,
    snackbarState: SnackbarHostState,
    event: (AppEvents) -> Unit,
    backScreen: () -> Unit,
) {
    var showContinueScrollButton by remember { mutableStateOf(false) }

    with(whatIsPlayingState) {
        if (searchUrl.isNullOrEmpty() ||
            songName.isNullOrEmpty() ||
            musicDurationInMiliSec == null
        ) {
            event.invoke(AppEvents.BackScreen)
            return
        }
    }
    // Nao funciona
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
                AnimatedVisibility(showContinueScrollButton) {
                    CifraFAB(type = FABType.CONTINUE_SCROLL) {
                        showContinueScrollButton = false
                    }
                }

                Spacer(Modifier.height(10.dp))
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
                    whatIsPlayingState.musicDurationInMiliSec!!,
                    showContinueScrollButton
                ) { showScrollButton ->
                    showContinueScrollButton = showScrollButton
                }
            }
        })

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WebViewComponent(
    searchUrl: String,
    musicDurationInMiliSeconds: Int,
    showScrollButton: Boolean,
    showContinueScrollButton: (Boolean) -> Unit
) {
    var pageHeight by remember { mutableIntStateOf(0) }
    val animator = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        factory = { context ->
            webViewClientConfig(context, searchUrl) { height ->
                pageHeight = height
            }
        },
        update = { webView ->
            webView.scrollTo(0, animator.value.toInt())
        },
        modifier = Modifier.pointerInteropFilter { motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
                    coroutineScope.launch {
                        showContinueScrollButton(true)
                        animator.stop()
                    }
                }
            }
            false
        }
    )
    val targetScroll = pageHeight * 0.75f // 3/4 da página
    val scrollDuration = musicDurationInMiliSeconds.div(4) // Duração proporcional

    LaunchedEffect(pageHeight, showScrollButton) {
        if (pageHeight > 0 && !showScrollButton) {
            animator.animateTo(
                targetValue = targetScroll, // 3/4 da altura total
                animationSpec = tween(
                    durationMillis = scrollDuration.toInt(), // Tempo proporcional
                    easing = LinearEasing
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            //animator.stop()
        }
    }

}


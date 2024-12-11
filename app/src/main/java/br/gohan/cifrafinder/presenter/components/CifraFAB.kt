package br.gohan.cifrafinder.presenter.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme
import br.gohan.cifrafinder.presenter.theme.Green
import kotlin.math.roundToInt

@Composable
fun CifraFAB(type: FABType, callback: () -> Unit) {
    val icon = when (type) {
        FABType.LOG_OFF -> Icons.Rounded.ExitToApp
        FABType.REFRESH -> Icons.Rounded.Refresh
        FABType.BACK -> Icons.Rounded.ArrowBack
    }
    val offsetSaver = Saver<Offset, List<Float>>(
        save = { listOf(it.x, it.y) },
        restore = { Offset(it[0], it[1]) }
    )

    var fabOffset by rememberSaveable(stateSaver = offsetSaver) {
        mutableStateOf(Offset(0f, 0f))
    }
    val hapticFeedback = LocalHapticFeedback.current

    CifraFinderTheme {
        FloatingActionButton(
            containerColor = Green,
            modifier = Modifier
                .size(70.dp)
                .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        fabOffset += dragAmount
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
            shape = RoundedCornerShape(20.dp),
            onClick = {
                callback.invoke()
            }) {
            Icon(
                icon,
                contentDescription = stringResource(id = R.string.description_icon_check),
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

enum class FABType {
    LOG_OFF,
    REFRESH,
    BACK
}
package br.gohan.cifrafinder.presenter.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.gohan.cifrafinder.R

@Composable
fun CifraFAB(type: FABType, callback: () -> Unit) {
    val icon = when (type) {
        FABType.LOG_OFF -> Icons.Rounded.ExitToApp
        FABType.REFRESH -> Icons.Rounded.Refresh
    }
    FloatingActionButton(
        modifier = Modifier.padding(bottom = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            callback.invoke()
        }) {
        Icon(
            icon,
            contentDescription = stringResource(id = R.string.description_icon_check),
            modifier = Modifier.size(30.dp)
        )
    }
}

enum class FABType {
    LOG_OFF,
    REFRESH
}
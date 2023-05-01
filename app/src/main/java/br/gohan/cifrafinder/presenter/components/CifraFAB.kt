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
import br.gohan.cifrafinder.presenter.theme.CifraFinderTheme
import br.gohan.cifrafinder.presenter.theme.Green

@Composable
fun CifraFAB(type: FABType, callback: () -> Unit) {
    val icon = when (type) {
        FABType.LOG_OFF -> Icons.Rounded.ExitToApp
        FABType.REFRESH -> Icons.Rounded.Refresh
    }
    CifraFinderTheme {
        FloatingActionButton(
            containerColor = Green,
            modifier = Modifier
                .size(70.dp),
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
    REFRESH
}
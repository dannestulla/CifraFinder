package br.gohan.cifrafinder.presenter.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NormalButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    string: Int,
    onClick: () -> Unit
) {
    ElevatedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(),
        onClick = {
            onClick.invoke()
        }) {
        if (isEnabled) {
            Text(
                stringResource(id = string),
                fontSize = 20.sp
            )
        } else {
            CircularProgressIndicator()
        }
    }
}
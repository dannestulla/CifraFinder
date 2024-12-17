package br.gohan.cifrafinder.presenter.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme

@Composable
fun MessageDialog(
    string: Int,
    dialogCallback: (Boolean) -> Unit
) {
    CifraFinderTheme {
        AlertDialog(onDismissRequest = { dialogCallback(false) },
            text = {
                Text(
                    text = stringResource(id = string),
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dialogCallback.invoke(true)
                    }) {
                    Text(text = stringResource(id = R.string.log_off_yes))
                }
            }, dismissButton = {
                Button(
                    onClick = {
                        dialogCallback(false)
                    }) {
                    Text(text = stringResource(id = R.string.log_off_no))
                }
            })
    }
}
package br.gohan.cifrafinder.presenter.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import br.gohan.cifrafinder.R
import br.gohan.cifrafinder.presenter.ui.theme.CifraFinderTheme

@Composable
fun LogoutDialog(
    isDialogOn: MutableState<Boolean>,
    dialogCallback: () -> Unit
) {
    CifraFinderTheme {
    AlertDialog(onDismissRequest = { isDialogOn.value = false },
        text = {
            Text(
                text = stringResource(id = R.string.log_off_dialog_title),
                fontSize = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    isDialogOn.value = false
                    dialogCallback.invoke()
                }) {
                Text(text = stringResource(id = R.string.log_off_yes))
            }
        }, dismissButton = {
            Button(
                onClick = {
                    isDialogOn.value = false
                }) {
                Text(text = stringResource(id = R.string.log_off_no))
            }
        })}
}
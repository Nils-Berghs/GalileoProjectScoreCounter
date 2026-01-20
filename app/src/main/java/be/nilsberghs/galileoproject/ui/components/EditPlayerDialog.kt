package be.nilsberghs.galileoproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import be.nilsberghs.galileoproject.R
import be.nilsberghs.galileoproject.data.Player
import be.nilsberghs.galileoproject.util.AddPlayerResult
import kotlinx.coroutines.launch

@Composable
fun EditPlayerDialog(
    player: Player,
    onDismiss: () -> Unit,
    onConfirm: suspend  (String) -> AddPlayerResult
) {

    val state = rememberTextFieldState(player.name)
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    val onAdd = {
        if (state.text.isNotBlank()) {

            scope.launch {
                when (val result = onConfirm(state.text.toString().trim())){
                    is AddPlayerResult.Success -> onDismiss()
                    is AddPlayerResult.AlreadyExists -> {
                        errorMessage = context.getString(R.string.player_exists)
                    }
                    is AddPlayerResult.DeletedExists -> {
                        errorMessage = context.getString((R.string.deleted_player_exists))
                    }
                }

            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_edit_player_title)) },
        text = {
            OutlinedTextField(
                state = state,
                inputTransformation = InputTransformation.maxLength(25),
                label = { Text(stringResource(R.string.label_player_name)) },
                lineLimits = TextFieldLineLimits.SingleLine,
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words
                ),
                onKeyboardAction = { onAdd() }
            )
        },
        confirmButton = {
            Button(
                onClick = onAdd,
                enabled = state.text.isNotBlank()
            ) { Text(stringResource(R.string.action_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

package be.nilsberghs.galileoproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSelectionScreen(
    viewModel: ScoreViewModel,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allPlayers by viewModel.allPlayers.collectAsState()
    val selectedPlayers by viewModel.selectedPlayers.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Select Players (Max 4)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(allPlayers) { player ->
                val isSelected = selectedPlayers.contains(player)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.togglePlayerSelection(player) }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { viewModel.togglePlayerSelection(player) },
                        enabled = (!player.isDeleted) && (isSelected || selectedPlayers.size < 4)
                    )
                    Text(
                        text = player.name,
                        textDecoration = if (player.isDeleted) TextDecoration.LineThrough else TextDecoration.None,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (player.isDeleted) {
                        IconButton(
                            onClick = { viewModel.restorePlayer(player) },
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restore Player")
                        }
                    }
                    else {
                        IconButton(
                            onClick = { viewModel.deletePlayer(player) },
                            enabled = !isSelected
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Player")
                        }
                    }
                }
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedPlayers.isNotEmpty()
        ) {
            Text(text = "Start Game (${selectedPlayers.size}/4)")
        }
    }
}

@Composable
fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    val onAdd = {
        if (name.isNotBlank()) {
            onConfirm(name)
            onDismiss()
        }
    }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add New Player") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAdd() })
                )
            },
        confirmButton = {
            Button(
                onClick = onAdd,
                enabled = name.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

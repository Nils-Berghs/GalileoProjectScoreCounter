package be.nilsberghs.galileoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import be.nilsberghs.galileoproject.data.Player

@Composable
fun EditPlayersScreen(
    viewModel: ScoreViewModel,
    modifier: Modifier = Modifier
) {
    val allPlayers by viewModel.allPlayers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    // Replace showEditDialog with this:
    var editingPlayer by remember { mutableStateOf<Player?>(null) }

    if (showAddDialog) {
        AddPlayerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name -> viewModel.addPlayerToDatabase(name) }
        )
    }

    editingPlayer?.let { player ->
        EditPlayerDialog(
            player = player, // Pass the player object
            onDismiss = { editingPlayer = null },
            onConfirm = { newName ->
                viewModel.updatePlayerName(player, newName)
                editingPlayer = null
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allPlayers) { player ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
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
                        } else {
                            IconButton(
                                onClick = { editingPlayer = player }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Player")
                            }
                            IconButton(
                                onClick = { viewModel.deletePlayer(player) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Player")
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Player")
        }
    }
}

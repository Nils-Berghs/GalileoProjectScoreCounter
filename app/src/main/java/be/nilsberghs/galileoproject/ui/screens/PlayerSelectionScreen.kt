package be.nilsberghs.galileoproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import be.nilsberghs.galileoproject.R
import be.nilsberghs.galileoproject.ScoreViewModel

@Composable
fun PlayerSelectionScreen(
    viewModel: ScoreViewModel,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlayers by viewModel.activePlayers.collectAsState()
    val selectedPlayers by viewModel.selectedPlayers.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(activePlayers) { player ->
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
                        enabled = isSelected || selectedPlayers.size < 4
                    )
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedPlayers.size in 2..4
        ) {
            Text(text = stringResource(R.string.action_start_game, selectedPlayers.size))
        }
    }
}

package be.nilsberghs.galileoproject.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import be.nilsberghs.galileoproject.R
import be.nilsberghs.galileoproject.ScoreViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: ScoreViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.fullHistory.collectAsState()
    val selectedGameId by viewModel.selectedHistoryGameId.collectAsState()
    val historyScores by viewModel.historyScores.collectAsState()
    val context = LocalContext.current
    val dateTimeFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    if (selectedGameId != null) {
        BackHandler {
            viewModel.selectHistoryGame(null)
        }

        GameScoringContent(
            scores = historyScores.map { it.score },
            players = historyScores.map { it.player },
            onScoreChange = { _, _, _ -> }, // Read-only
            onFinishGame = { viewModel.selectHistoryGame(null) },
            modifier = modifier,
            isReadOnly = true
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(games) { history ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { viewModel.selectHistoryGame(history.game.id) }
                    ) {
                        val maxScore = history.scores.maxOfOrNull { it.score.total } ?: 0
                        val winners = history.scores.filter { it.score.total == maxScore }
                        val isInvalidGame = maxScore == 0 || winners.isEmpty()
                        val isDraw = winners.size > 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isInvalidGame) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = stringResource(R.string.label_unknown),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.label_unknown),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = stringResource(R.string.label_winner),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                        )
                                        if (isDraw) {
                                            Text(
                                                text = winners.joinToString(", ") { it.player.name },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        } else {
                                            val winner = winners.first()
                                            Text(
                                                text = winner.player.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = " " + stringResource(R.string.label_pts, winner.score.total),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val formattedDate = dateTimeFormatter.format(Date(history.game.startTime))
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(R.string.label_game_number, history.game.id),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

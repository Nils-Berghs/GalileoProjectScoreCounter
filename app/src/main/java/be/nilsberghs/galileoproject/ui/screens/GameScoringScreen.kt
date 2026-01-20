package be.nilsberghs.galileoproject.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nilsberghs.galileoproject.R
import be.nilsberghs.galileoproject.ScoreViewModel
import be.nilsberghs.galileoproject.data.Player
import be.nilsberghs.galileoproject.data.ScoreEntry
import be.nilsberghs.galileoproject.ui.theme.CallistoBrown
import be.nilsberghs.galileoproject.ui.theme.EuropaYellow
import be.nilsberghs.galileoproject.ui.theme.GanymedeGrey
import be.nilsberghs.galileoproject.ui.theme.IoOrange
import kotlinx.coroutines.delay

@Composable
fun GameScoringScreen(
    viewModel: ScoreViewModel,
    onFinishGame: () -> Unit,
    modifier: Modifier
) {
    val scores by viewModel.currentScores.collectAsState()
    val players by viewModel.selectedPlayers.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (viewModel.hasAnyScores()) {
            showCancelDialog = true
        } else {
            viewModel.cancelGame()
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(stringResource(R.string.dialog_cancel_game_title)) },
            text = { Text(stringResource(R.string.dialog_cancel_game_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelGame()
                }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.action_keep_scoring))
                }
            }
        )
    }

    GameScoringContent(
        scores = scores,
        players = players,
        onScoreChange = { scoreEntry, category, newValue ->
            viewModel.updateScore(scoreEntry, category, newValue)
        },
        onFinishGame = onFinishGame,
        modifier = modifier
    )
}

@Composable
fun GameScoringContent(
    scores: List<ScoreEntry>,
    players: List<Player>,
    onScoreChange: (ScoreEntry, String, Int) -> Unit,
    onFinishGame: () -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false
) {
    val categories = listOf(
        CategoryInfo("io", R.drawable.ic_io, stringResource(R.string.cat_io), null),
        CategoryInfo("europa", R.drawable.ic_europa, stringResource(R.string.cat_europa), null),
        CategoryInfo("ganymede", R.drawable.ic_ganymede, stringResource(R.string.cat_ganymede), null),
        CategoryInfo("callisto", R.drawable.ic_callisto, stringResource(R.string.cat_callisto), null),
        CategoryInfo("assistants", R.drawable.ic_assistant, stringResource(R.string.cat_assistants), null),
        CategoryInfo("tech", R.drawable.ic_tech, stringResource(R.string.cat_tech), null),
        CategoryInfo("achievements", R.drawable.ic_achievement, stringResource(R.string.cat_achievements), MaterialTheme.colorScheme.onBackground),
    )

    val configuration = LocalConfiguration.current
    val scrollState = rememberScrollState()
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val scrollModifier = if (isLandscape) Modifier.verticalScroll(scrollState) else Modifier
    val canFinish = isReadOnly || (scores.isNotEmpty() && scores.all { it.total > 0 })

    if (isLandscape) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .then(scrollModifier)
        ) {
            // top row with Icons
            Row(verticalAlignment = Alignment.CenterVertically) {

                Spacer(Modifier.weight(0.7f))

                categories.forEach { cat ->

                    val iconPainter = painterResource(id = cat.resId)

                    Icon(
                        painter = iconPainter,
                        contentDescription = cat.name,
                        tint = cat.color ?: Color.Unspecified,
                        modifier = Modifier
                            .size(60.dp)
                            .weight(0.6f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(R.string.nav_history),
                    modifier = Modifier
                        .weight(0.7f)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            //row per player
            players.forEach { player ->
                val scoreEntry = scores.find { it.playerId == player.id }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    categories.forEach { cat ->
                        if (scoreEntry != null) {
                            ScoreInputCell(
                                value = getVal(scoreEntry, cat.id),
                                onValueChange = { newValue ->
                                    onScoreChange(scoreEntry, cat.id, newValue)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                enabled = !isReadOnly
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }

                    Text(
                        text = scoreEntry?.total?.toString() ?: "0",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Button(
                onClick = onFinishGame,
                modifier = Modifier.fillMaxWidth(),
                enabled = canFinish
            ) {
                Text(if (isReadOnly) stringResource(R.string.action_back) else stringResource(R.string.action_finish_game))
            }
        }
    } else {
        //portrait layout
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(0.7f))
                players.forEach { player ->
                    Text(
                        text = player.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp)

            categories.forEach { cat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 2.dp)
                ) {
                    val iconPainter = painterResource(id = cat.resId)

                    Icon(
                        painter = iconPainter,
                        contentDescription = cat.name,
                        tint = cat.color ?: Color.Unspecified,
                        modifier = Modifier
                            .size(60.dp)
                            .weight(0.6f)
                    )

                    players.forEach { player ->
                        val scoreEntry = scores.find { it.playerId == player.id }

                        if (scoreEntry != null) {
                            ScoreInputCell(
                                value = getVal(scoreEntry, cat.id),
                                onValueChange = { newValue ->
                                    onScoreChange(scoreEntry, cat.id, newValue)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                enabled = !isReadOnly
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(R.string.total_score),
                    modifier = Modifier
                        .weight(0.7f)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                players.forEach { player ->
                    val scoreEntry = scores.find { it.playerId == player.id }
                    Text(
                        text = scoreEntry?.total?.toString() ?: "0",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))



            Button(
                onClick = onFinishGame,
                modifier = Modifier.fillMaxWidth(),
                enabled = canFinish
            ) {
                Text(if (isReadOnly) stringResource(R.string.action_back) else stringResource(R.string.action_finish_game))
            }
        }
    }
}

@Composable
fun ScoreInputCell(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var textFieldValue by remember { mutableStateOf(
        TextFieldValue(
            if (value == 0) "" else value.toString()
        )
    )}

    LaunchedEffect(value, textFieldValue) {
        delay(500L)
        if ((textFieldValue.text.toIntOrNull() ?: 0) != value) {
            val newText = if (value == 0) "" else value.toString()
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(newText.length) // Reset cursor to the end
            )
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { newValue ->    // Only update if input is empty or a valid number
            val text = newValue.text.trimStart('0')
            if (text.isEmpty() || text.toIntOrNull() != null) {

                textFieldValue = newValue.copy(text = text)
                onValueChange(text.toIntOrNull() ?: 0)
            }
        },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        modifier = modifier.padding(horizontal = 2.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

private fun getVal(entry: ScoreEntry, id: String) = when (id) {
    "io" -> entry.io
    "europa" -> entry.europa
    "ganymede" -> entry.ganymede
    "callisto" -> entry.callisto
    "tech" -> entry.technologies
    "achievements" -> entry.achievements
    "assistants" -> entry.assistants
    else -> 0
}

data class CategoryInfo(
    val id: String,
    val resId: Int,
    val name: String,
    val color: Color?
)

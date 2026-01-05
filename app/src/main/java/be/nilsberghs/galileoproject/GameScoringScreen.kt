package be.nilsberghs.galileoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nilsberghs.galileoproject.data.ScoreEntry

@Composable
fun GameScoringScreen(viewModel: ScoreViewModel, onFinishGame: ()-> Unit, modifier: Modifier){
    val scores by viewModel.currentScores.collectAsState()
    val players by viewModel.selectedPlayers.collectAsState()

    // Map categories to Icons for the header column
    val categories = listOf(
        CategoryInfo("io", Icons.Default.BrightnessLow, "Io"),
        CategoryInfo("europa", Icons.Default.BrightnessMedium, "Europa"),
        CategoryInfo("ganymede", Icons.Default.BrightnessHigh, "Ganymede"),
        CategoryInfo("callisto", Icons.Default.Brightness7, "Callisto"),
        CategoryInfo("tech", Icons.Default.Settings, "Tech"),
        CategoryInfo("achievements", Icons.Default.EmojiEvents, "Achievements"),
        CategoryInfo("assistants", Icons.Default.Groups, "Assistants")
    )

    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp)
        ) {
        // 1. TOP ROW: Player Names (Tiny or First Letter)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(0.7f)) // Category column space
            players.forEach { player ->
                Text(
                    text = player.name.take(3).uppercase(), // Show first 3 letters
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // 2. SCORING ROWS
        categories.forEach { cat ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Category Icon Column
                Icon(
                    imageVector = cat.icon,
                    contentDescription = cat.name,
                    modifier = Modifier
                        .weight(0.7f)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                // Input Cells
                scores.forEach { scoreEntry ->
                    ScoreInputCell(
                        value = getVal(scoreEntry, cat.id),
                        onValueChange = { newValue ->
                            viewModel.updateScore(scoreEntry, cat.id, newValue)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 3. TOTAL ROW
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Σ", // Sum symbol for total
                modifier = Modifier.weight(0.7f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            scores.forEach { scoreEntry ->
                Text(
                    text = scoreEntry.total.toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onFinishGame, modifier = Modifier.fillMaxWidth()) {
            Text("Finish Game")
        }
    }
}

@Composable
fun ScoreInputCell(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    // We use a String state to allow empty input while typing
    var textValue by remember(value) { mutableStateOf(if (value == 0) "" else value.toString()) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicTextField(
            value = textValue,
            onValueChange = {
                if (it.isEmpty() || it.toIntOrNull() != null) {
                    textValue = it
                    onValueChange(it.toIntOrNull() ?: 0)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .width(40.dp)
                .padding(4.dp)
        )
    }
}

private fun getVal(entry: ScoreEntry, id: String) = when(id) {
    "io" -> entry.io
    "europa" -> entry.europa
    "ganymede" -> entry.ganymede
    "callisto" -> entry.callisto
    "tech" -> entry.technologies
    "achievements" -> entry.achievements
    "assistants" -> entry.assistants
    else -> 0
}

data class CategoryInfo(val id: String, val icon: ImageVector, val name: String)
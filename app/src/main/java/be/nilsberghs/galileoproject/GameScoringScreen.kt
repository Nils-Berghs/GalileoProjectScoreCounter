package be.nilsberghs.galileoproject

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nilsberghs.galileoproject.data.ScoreEntry
import be.nilsberghs.galileoproject.ui.theme.AchievementWhite
import be.nilsberghs.galileoproject.ui.theme.CallistoBrown
import be.nilsberghs.galileoproject.ui.theme.EuropaYellow
import be.nilsberghs.galileoproject.ui.theme.GalileoPink
import be.nilsberghs.galileoproject.ui.theme.GalileoPurple
import be.nilsberghs.galileoproject.ui.theme.GanymedeGrey
import be.nilsberghs.galileoproject.ui.theme.IoOrange

@Composable
fun GameScoringScreen(
    viewModel: ScoreViewModel,
    onFinishGame: () -> Unit,
    modifier: Modifier
) {
    val scores by viewModel.currentScores.collectAsState()
    val players by viewModel.selectedPlayers.collectAsState()

    val categories = listOf(
        CategoryInfo("io", null, Icons.Filled.Circle, "Io", IoOrange),
        CategoryInfo("europa", null, Icons.Filled.Circle, "Europa", EuropaYellow),
        CategoryInfo("ganymede", null, Icons.Filled.Circle, "Ganymede", GanymedeGrey),
        CategoryInfo("callisto", null, Icons.Filled.Circle, "Callisto", CallistoBrown),
        CategoryInfo("assistants", R.drawable.ic_assistant, null, "Assistants", null),
        CategoryInfo("tech", R.drawable.ic_tech, null, "Technologies", null),
        CategoryInfo("achievements", R.drawable.ic_achievement, null, "Achievements", AchievementWhite),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 1. TOP ROW: Player Names
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

        // 2. SCORING ROWS
        categories.forEach { cat ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min) // Row height matches the tallest child (TextField)
                    .padding(vertical = 2.dp)
            ) {
                val iconPainter = if (cat.resId != null) {
                    painterResource(id = cat.resId)
                } else {
                    rememberVectorPainter(image = cat.vector!!)
                }

                Icon(
                    painter = iconPainter,
                    contentDescription = cat.name,
                    tint = cat.color ?: Color.Unspecified,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight() // Fill the row height
                        .padding(4.dp) // Internal icon padding
                )

                scores.forEach { scoreEntry ->
                    ScoreInputCell(
                        value = getVal(scoreEntry, cat.id),
                        onValueChange = { newValue ->
                            viewModel.updateScore(scoreEntry, cat.id, newValue)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 3. TOTAL ROW
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Σ",
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
    var textValue by remember(value) { mutableStateOf(if (value == 0) "" else value.toString()) }

    TextField(
        value = textValue,
        onValueChange = {
            if (it.isEmpty() || it.toIntOrNull() != null) {
                textValue = it
                onValueChange(it.toIntOrNull() ?: 0)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.padding(horizontal = 2.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
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
    val resId: Int?,
    val vector: ImageVector?,
    val name: String,
    val color: Color?
)

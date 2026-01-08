package be.nilsberghs.galileoproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import be.nilsberghs.galileoproject.ui.GameScoringScreen
import be.nilsberghs.galileoproject.ui.theme.GalileoProjectTheme

enum class Screen {
    History, NewGame, EditPlayers
}

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalileoProjectTheme {
                var currentScreen by remember { mutableStateOf(Screen.NewGame) }
                var showAddDialog by remember { mutableStateOf(false) }
                var showDeleteConfirm by remember { mutableStateOf(false) }
                val currentGameId by viewModel.currentGameId.collectAsState()
                val selectedHistoryGameId by viewModel.selectedHistoryGameId.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    when (currentScreen) {
                                        Screen.History -> if (selectedHistoryGameId != null) "Game Detail" else "Previous Games"
                                        Screen.NewGame -> "Select Players (max 4)"
                                        Screen.EditPlayers -> "Manage Players"
                                    }
                                )
                            },
                            actions = {
                                if (currentGameId == null) {
                                    if (currentScreen == Screen.NewGame) {
                                        IconButton(onClick = { showAddDialog = true }) {
                                            Icon(Icons.Default.Add, contentDescription = "Add Player")
                                        }
                                    } else if (currentScreen == Screen.History && selectedHistoryGameId != null) {
                                        IconButton(onClick = { showDeleteConfirm = true }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Game")
                                        }
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (currentGameId == null) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.History,
                                    onClick = { currentScreen = Screen.History },
                                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                                    label = { Text("History") }
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.NewGame,
                                    onClick = { currentScreen = Screen.NewGame },
                                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                                    label = { Text("New Game") }
                                )
                                NavigationBarItem(
                                    selected = currentScreen == Screen.EditPlayers,
                                    onClick = { currentScreen = Screen.EditPlayers },
                                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                                    label = { Text("Players") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    if (showAddDialog) {
                        AddPlayerDialog(
                            onDismiss = { showAddDialog = false },
                            onConfirm = { name -> viewModel.addPlayerToDatabase(name) }
                        )
                    }

                    if (showDeleteConfirm) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text("Delete Game?") },
                            text = { Text("Are you sure you want to delete this game record? This action cannot be undone.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    selectedHistoryGameId?.let { viewModel.deleteHistoryGame(it) }
                                    showDeleteConfirm = false
                                }) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    if (currentGameId == null) {
                        when (currentScreen) {
                            Screen.History -> {
                                HistoryScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            Screen.NewGame -> PlayerSelectionScreen(
                                viewModel = viewModel,
                                onStartGame = { viewModel.startNewGame() },
                                modifier = Modifier.padding(innerPadding)
                            )
                            Screen.EditPlayers -> EditPlayersScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    } else {
                        GameScoringScreen(
                            viewModel = viewModel,
                            onFinishGame = viewModel::finishGame,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

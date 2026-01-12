package be.nilsberghs.galileoproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import be.nilsberghs.galileoproject.ui.components.AddPlayerDialog
import be.nilsberghs.galileoproject.ui.screens.AboutScreen
import be.nilsberghs.galileoproject.ui.screens.EditPlayersScreen
import be.nilsberghs.galileoproject.ui.screens.GameScoringScreen
import be.nilsberghs.galileoproject.ui.screens.HistoryScreen
import be.nilsberghs.galileoproject.ui.screens.PlayerSelectionScreen
import be.nilsberghs.galileoproject.ui.screens.SettingsScreen
import be.nilsberghs.galileoproject.ui.theme.GalileoProjectTheme

enum class Screen {
    History, NewGame, EditPlayers, Settings, About
}

class MainActivity : AppCompatActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalileoProjectTheme {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.NewGame) }
                var previousScreen by rememberSaveable { mutableStateOf(Screen.NewGame) }
                var showAddDialog by remember { mutableStateOf(false) }
                var showDeleteConfirm by remember { mutableStateOf(false) }
                val currentGameId by viewModel.currentGameId.collectAsState()
                val selectedHistoryGameId by viewModel.selectedHistoryGameId.collectAsState()
                val nullableAllPlayers by viewModel.nullableAllPlayers.collectAsState()

                var hasRedirected by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(nullableAllPlayers, currentGameId) {
                    val players = nullableAllPlayers ?: return@LaunchedEffect
                    if (!hasRedirected) {
                        if (currentGameId == null) {
                            if (players.isEmpty()) {
                                currentScreen = Screen.EditPlayers
                            }
                        } else {
                            currentScreen = Screen.NewGame
                        }
                        hasRedirected = true
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_jupiter),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.22f),
                        contentScale = ContentScale.Crop
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        when (currentScreen) {
                                            Screen.History -> if (selectedHistoryGameId != null) stringResource(R.string.title_game_detail) else stringResource(R.string.title_previous_games)
                                            Screen.NewGame -> if (currentGameId != null) stringResource(R.string.title_game_scoring) else stringResource(R.string.title_select_players)
                                            Screen.EditPlayers -> stringResource(R.string.title_manage_players)
                                            Screen.Settings -> stringResource(R.string.title_settings)
                                            Screen.About -> stringResource(R.string.title_about)
                                        }
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    scrolledContainerColor = Color.Transparent
                                ),
                                actions = {
                                    if (currentGameId == null) {
                                        if (currentScreen == Screen.NewGame) {
                                            IconButton(onClick = { showAddDialog = true }) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = stringResource(R.string.dialog_add_player_title)
                                                )
                                            }
                                        } else if (currentScreen == Screen.History && selectedHistoryGameId != null) {
                                            IconButton(onClick = { showDeleteConfirm = true }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = stringResource(R.string.action_delete)
                                                )
                                            }
                                        }
                                    }
                                    if (currentScreen != Screen.Settings) {
                                        IconButton(onClick = {
                                            previousScreen = currentScreen
                                            currentScreen = Screen.Settings
                                        }) {
                                            Icon(
                                                Icons.Default.Settings,
                                                contentDescription = stringResource(R.string.desc_settings)
                                            )
                                        }
                                    }

                                }
                            )
                        },
                        bottomBar = {
                            if (currentGameId == null) {
                                NavigationBar(
                                    containerColor = Color.Transparent,
                                    tonalElevation = 0.dp,
                                ) {
                                    NavigationBarItem(
                                        selected = currentScreen == Screen.History,
                                        onClick = { currentScreen = Screen.History },
                                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                                        label = { Text(stringResource(R.string.nav_history)) }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == Screen.NewGame,
                                        onClick = { currentScreen = Screen.NewGame },
                                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                                        label = { Text(stringResource(R.string.nav_new_game)) }
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == Screen.EditPlayers,
                                        onClick = { currentScreen = Screen.EditPlayers },
                                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                                        label = { Text(stringResource(R.string.nav_players)) }
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
                                title = { Text(stringResource(R.string.dialog_delete_game_title)) },
                                text = { Text(stringResource(R.string.dialog_delete_game_message)) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        selectedHistoryGameId?.let { viewModel.deleteHistoryGame(it) }
                                        showDeleteConfirm = false
                                    }) {
                                        Text(stringResource(R.string.action_delete))
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteConfirm = false }) {
                                        Text(stringResource(R.string.action_cancel))
                                    }
                                }
                            )
                        }

                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
                                Screen.History -> HistoryScreen(viewModel, modifier = Modifier)
                                Screen.EditPlayers -> EditPlayersScreen(viewModel, modifier = Modifier)
                                Screen.Settings -> SettingsScreen(
                                    viewModel = viewModel,
                                    onBackClick = { currentScreen = previousScreen },
                                    onAboutClick = { currentScreen = Screen.About },
                                    modifier = Modifier
                                )
                                Screen.About -> AboutScreen(
                                    onBackClick = { currentScreen = Screen.Settings },
                                    modifier = Modifier)
                                Screen.NewGame -> {
                                    if (currentGameId == null) {
                                        PlayerSelectionScreen(
                                            viewModel = viewModel,
                                            onStartGame = { viewModel.startNewGame() },
                                            modifier = Modifier
                                        )
                                    } else {
                                        GameScoringScreen(
                                            viewModel = viewModel,
                                            onFinishGame = viewModel::finishGame,
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

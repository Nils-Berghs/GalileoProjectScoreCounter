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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import be.nilsberghs.galileoproject.ui.theme.GalileoProjectTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalileoProjectTheme {

                // State to control whether the dropdown menu is visible
                var showMenu by remember { mutableStateOf(false) }
                // Observe the "show deleted" state from the ViewModel
                val isShowingDeleted by viewModel.showDeleted.collectAsState()
                var showAddDialog by remember { mutableStateOf(false) }
                val currentGameId by viewModel.currentGameId.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Galileo Project") },
                            actions = {
                                if (currentGameId == null) {
                                    IconButton(onClick = { showAddDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Player")
                                    }
                                    IconButton(onClick = { showMenu = true }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Options"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(if (isShowingDeleted) "Hide Deleted" else "Show Deleted")
                                            },
                                            onClick = {
                                                viewModel.toggleShowDeleted()
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    },

                ) { innerPadding ->

                    if (showAddDialog) {
                        AddPlayerDialog(
                            onDismiss = { showAddDialog = false },
                            onConfirm = { name -> viewModel.addPlayerToDatabase(name) }
                        )
                    }

                    val currentGameId by viewModel.currentGameId.collectAsState()
                    if (currentGameId == null) {
                        PlayerSelectionScreen(
                            viewModel = viewModel,
                            onStartGame = { viewModel.startNewGame() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        GameScoringScreen(viewModel, viewModel::finishGame, Modifier.padding(innerPadding) )
                    }
                }
            }
        }
    }
}

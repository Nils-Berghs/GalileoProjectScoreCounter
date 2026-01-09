package be.nilsberghs.galileoproject

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.nilsberghs.galileoproject.data.AppDatabase
import be.nilsberghs.galileoproject.data.Game
import be.nilsberghs.galileoproject.data.GameHistory
import be.nilsberghs.galileoproject.data.Player
import be.nilsberghs.galileoproject.data.ScoreEntry
import be.nilsberghs.galileoproject.data.ScoreWithPlayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val LOGTAG = "ScoreViewModel"
    private val playerDao = AppDatabase.getDatabase(application).playerDao()
    private val gameDao = AppDatabase.getDatabase(application).gameDao()

    private val _selectedPlayers = MutableStateFlow<List<Player>>(emptyList())
    val selectedPlayers: StateFlow<List<Player>> = _selectedPlayers.asStateFlow()

    private  val _currentGameId = MutableStateFlow<Int?>(null)
    var currentGameId = _currentGameId.asStateFlow()

    // History selection
    private val _selectedHistoryGameId = MutableStateFlow<Int?>(null)
    val selectedHistoryGameId = _selectedHistoryGameId.asStateFlow()

    val allPlayers: StateFlow<List<Player>> = playerDao.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val nullableAllPlayers: StateFlow<List<Player>?> = playerDao.getAllPlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Start with null to indicate "Loading"
        )

    val activePlayers: StateFlow<List<Player>> = playerDao.getActivePlayers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe scores for the current game
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentScores: StateFlow<List<ScoreEntry>> = _currentGameId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else gameDao.getScoresForGame(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyScores: StateFlow<List<ScoreWithPlayer>> = _selectedHistoryGameId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else gameDao.getScoresWithPlayers(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fullHistory: StateFlow<List<GameHistory>> = gameDao.getFullHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            try {
                Log.d(LOGTAG, "Checking for unfinished games...")
                val activeId = gameDao.getLatestActiveGameId()
                if (activeId != null) {
                    Log.d(LOGTAG, "Resuming unfinished game $activeId")
                    // Use the new List-based query to avoid Flow hanging during init
                    val entries = gameDao.getScoresForGameList(activeId) 
                    val players = gameDao.getPlayersForGame(activeId)

                    val orderedPlayers = entries.mapNotNull { entry ->
                        players.find { it.id == entry.playerId }
                    }
                    
                    if (orderedPlayers.isNotEmpty()) {
                        _selectedPlayers.value = orderedPlayers
                        Log.d(LOGTAG, "Setting current game Id to $activeId")
                        _currentGameId.value = activeId
                    } else {
                        Log.d(LOGTAG, "Game had no players, deleting $activeId")
                        gameDao.deleteGameById(activeId)
                    }
                }
            } catch (e: Exception) {
                Log.e(LOGTAG, "Error in init", e)
            }
        }
    }

    fun addPlayerToDatabase(name: String) {
        viewModelScope.launch {
            val id = playerDao.insert(Player(name = name))
            if (id != -1L){
                val newPlayer = Player(id = id.toInt(), name = name)
                togglePlayerSelection(newPlayer)
            }
        }
    }

    fun updatePlayerName(player: Player, newName: String) {
        viewModelScope.launch {
            val updatedPlayer = player.copy(name = newName)
            playerDao.update(updatedPlayer)

            val currentSelection = _selectedPlayers.value.toMutableList()
            val index = currentSelection.indexOfFirst { it.id == player.id }
            if (index != -1) {
                currentSelection[index] = updatedPlayer
                _selectedPlayers.value = currentSelection
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            _selectedPlayers.value = _selectedPlayers.value.filter { it.id != player.id }
            playerDao.softDeletePlayer(player.id)
        }
    }

    fun restorePlayer(player: Player) {
        viewModelScope.launch {
            playerDao.restorePlayer(player.id)
        }
    }

    fun togglePlayerSelection(player: Player) {
        val current = _selectedPlayers.value.toMutableList()
        if (current.contains(player)) {
            current.remove(player)
        } else if (current.size < 4) {
            current.add(player)
        }
        _selectedPlayers.value = current
    }

    fun startNewGame() {
        viewModelScope.launch {
            val gameId = gameDao.insertGame(Game()).toInt()
            selectedPlayers.value.forEach { player ->
                gameDao.insertScoreEntry(ScoreEntry(gameId = gameId, playerId = player.id))
            }
            _currentGameId.value = gameId
        }
    }

    fun updateScore(scoreEntry: ScoreEntry, category: String, newValue: Int) {
        viewModelScope.launch {
            val updated = when (category) {
                "io" -> scoreEntry.copy(io = newValue)
                "europa" -> scoreEntry.copy(europa = newValue)
                "ganymede" -> scoreEntry.copy(ganymede = newValue)
                "callisto" -> scoreEntry.copy(callisto = newValue)
                "tech" -> scoreEntry.copy(technologies = newValue)
                "achievements" -> scoreEntry.copy(achievements = newValue)
                "assistants" -> scoreEntry.copy(assistants = newValue)
                else -> scoreEntry
            }
            gameDao.updateScoreEntry(updated)
        }
    }

    fun finishGame() {
        viewModelScope.launch {
            _currentGameId.value?.let { id ->
                gameDao.markGameFinished(id)
            }
            _currentGameId.value = null
        }
    }

    fun cancelGame() {
        viewModelScope.launch {
            _currentGameId.value?.let { gameId ->
                gameDao.deleteGameById(gameId)
            }
            _currentGameId.value = null
        }
    }

    fun deleteHistoryGame(gameId: Int) {
        viewModelScope.launch {
            gameDao.deleteGameById(gameId)
            _selectedHistoryGameId.value = null
        }
    }

    fun selectHistoryGame(gameId: Int?) {
        _selectedHistoryGameId.value = gameId
    }

    fun hasAnyScores(): Boolean {
        return currentScores.value.any { it.total > 0 }
    }
}

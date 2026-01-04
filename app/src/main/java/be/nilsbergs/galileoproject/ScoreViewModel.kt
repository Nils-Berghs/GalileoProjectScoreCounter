package be.nilsbergs.galileoproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.nilsbergs.galileoproject.data.AppDatabase
import be.nilsbergs.galileoproject.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val playerDao = AppDatabase.getDatabase(application).playerDao()
    private val _showDeleted = MutableStateFlow(false)
    val showDeleted = _showDeleted.asStateFlow()


    val allPlayers: StateFlow<List<Player>> = playerDao.getAllPlayers()
        .combine(showDeleted) { players, showDeleted ->
            if (showDeleted) players else players.filter { !it.isDeleted }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPlayers = MutableStateFlow<List<Player>>(emptyList())
    val selectedPlayers: StateFlow<List<Player>> = _selectedPlayers.asStateFlow()

    /**
     * Persists a new player to the database and automatically adds them
     * to the current selection if the insertion was successful.
     *
     * @param name The display name of the player to be created.
     */
    fun addPlayerToDatabase(name: String) {
        viewModelScope.launch {
            val id = playerDao.insert(Player(name = name))
            if (id != -1L){
                val newPlayer = Player(id = id.toInt(), name = name)
                togglePlayerSelection(newPlayer)
            }

        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            playerDao.softDeletePlayer(player.id)
        }
    }

    fun toggleShowDeleted() {
        _showDeleted.value = !_showDeleted.value
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
}

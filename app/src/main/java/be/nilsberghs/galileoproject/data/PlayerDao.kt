package be.nilsberghs.galileoproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    // This will be used by EditPlayersScreen
    @Query("SELECT * FROM players ORDER BY isDeleted, name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    // This will be used by PlayerSelectionScreen
    @Query("SELECT * FROM players WHERE isDeleted = 0 ORDER BY name ASC")
    fun getActivePlayers(): Flow<List<Player>>

    @Query(value = "SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun getPlayerByName(name: String) : Player?


    @Query("UPDATE players SET isDeleted = 1 WHERE id = :playerId")
    suspend fun softDeletePlayer(playerId: Int)

    @Query("UPDATE players SET isDeleted = 0 WHERE id = :playerId")
    suspend fun restorePlayer(playerId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player): Long

    @Update
    suspend fun update(player: Player)
}

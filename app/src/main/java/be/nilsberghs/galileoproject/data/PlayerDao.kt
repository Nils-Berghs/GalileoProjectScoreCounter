package be.nilsberghs.galileoproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY id DESC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("UPDATE players SET isDeleted = 1 WHERE id = :playerId")
    suspend fun softDeletePlayer(playerId: Int)

    @Query("UPDATE players SET isDeleted = 0 WHERE id = :playerId")
    suspend fun restorePlayer(playerId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player): Long
}

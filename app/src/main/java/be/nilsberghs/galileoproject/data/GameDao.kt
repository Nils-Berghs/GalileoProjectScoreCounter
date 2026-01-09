package be.nilsberghs.galileoproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: Game): Long

    @Insert
    suspend fun insertScoreEntry(scoreEntry: ScoreEntry)

    @Update
    suspend fun updateScoreEntry(scoreEntry: ScoreEntry)

    @Query("SELECT * FROM scores WHERE gameId = :gameId")
    fun getScoresForGame(gameId: Int): Flow<List<ScoreEntry>>

    // New: One-time fetch for initialization
    @Query("SELECT * FROM scores WHERE gameId = :gameId")
    suspend fun getScoresForGameList(gameId: Int): List<ScoreEntry>

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    @Query("SELECT id FROM games WHERE isFinished = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getLatestActiveGameId(): Int?

    @Transaction
    @Query("SELECT players.* FROM players JOIN scores ON players.id = scores.playerId WHERE scores.gameId = :gameId")
    suspend fun getPlayersForGame(gameId: Int): List<Player>

    @Query("UPDATE games SET isFinished = 1 WHERE id = :gameId")
    suspend fun markGameFinished(gameId: Int)

    @Transaction
    @Query("SELECT * FROM games WHERE isFinished = 1 ORDER BY startTime DESC")
    fun getFullHistory(): Flow<List<GameHistory>>

    @Transaction
    @Query("SELECT * FROM scores WHERE gameId = :gameId")
    fun getScoresWithPlayers(gameId: Int): Flow<List<ScoreWithPlayer>>
}

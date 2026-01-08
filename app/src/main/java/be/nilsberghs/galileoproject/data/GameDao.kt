package be.nilsberghs.galileoproject.data

import androidx.room.Dao
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

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)

    @Query("SELECT * FROM games ORDER BY startTime DESC")
    fun getAllGames(): Flow<List<Game>>

    //TODO if this ever becomes to slow load with paging
    @Transaction
    @Query("SELECT * FROM games ORDER BY startTime DESC")
    fun getFullHistory(): Flow<List<GameHistory>>

    @Transaction
    @Query("SELECT * FROM scores WHERE gameId = :gameId")
    fun getScoresWithPlayers(gameId: Int): Flow<List<ScoreWithPlayer>>
}

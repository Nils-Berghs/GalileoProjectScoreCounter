package be.nilsberghs.galileoproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: Game): Long

    @Insert
    suspend fun insertScoreEntry(scoreEntry: ScoreEntry)

    @Update
    suspend fun updateScoreEntry(scoreEntry: ScoreEntry)

    @Query("SELECT * FROM score_entries WHERE gameId = :gameId")
    fun getScoresForGame(gameId: Int): Flow<List<ScoreEntry>>
}
package be.nilsberghs.galileoproject.data

import androidx.room.Embedded
import androidx.room.Relation

data class GameHistory(
    @Embedded val game: Game,
    @Relation(
        entity = ScoreEntry::class,
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val scores: List<ScoreWithPlayer>
)

data class ScoreWithPlayer(
    @Embedded val score: ScoreEntry,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: Player
)

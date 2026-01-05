package be.nilsberghs.galileoproject.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "scores",
    foreignKeys = [
        ForeignKey(entity = Game::class, parentColumns = ["id"], childColumns = ["gameId"]),
        ForeignKey(entity = Player::class, parentColumns = ["id"], childColumns = ["playerId"])
    ]
)
data class ScoreEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameId: Int,
    val playerId: Int,
    val io: Int = 0,
    val europa: Int = 0,
    val ganymede: Int = 0,
    val callisto: Int = 0,
    val technologies: Int = 0,
    val achievements: Int = 0,
    val assistants: Int = 0
) {
    // Helper to calculate the total for this player
    val total: Int get() = io + europa + ganymede + callisto + technologies + achievements + assistants
}



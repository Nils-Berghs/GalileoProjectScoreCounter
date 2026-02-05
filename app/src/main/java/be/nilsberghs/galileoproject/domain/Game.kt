package be.nilsberghs.galileoproject.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val isFinished: Boolean = false
)
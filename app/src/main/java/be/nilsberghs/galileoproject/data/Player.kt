package be.nilsberghs.galileoproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val name: String,
    val isDeleted: Boolean = false,
) {
    init {
        require(name.length <= 25) { "Player name cannot exceed 25 characters" }
    }
}

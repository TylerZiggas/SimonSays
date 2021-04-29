package edu.umsl.tyler.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "gameScore")
data class GameEntity(
        @ColumnInfo val difficulty: String,
        @ColumnInfo val score: Int,
        @ColumnInfo val gameDate: Date
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
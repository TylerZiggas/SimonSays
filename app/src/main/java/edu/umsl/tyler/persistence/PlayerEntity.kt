package edu.umsl.tyler.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @ColumnInfo val name: String,
    @ColumnInfo val score: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
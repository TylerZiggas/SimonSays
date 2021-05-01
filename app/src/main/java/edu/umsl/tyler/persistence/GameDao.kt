// Author: Tyler Ziggas
// Date: May 2021
// For database inserting and grabbing

package edu.umsl.tyler.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM gameScore ORDER BY score DESC")
    suspend fun fetchScoreboard(): List<GameEntity>

    @Query("SELECT max(id)+1 FROM gameScore")
    suspend fun fetchID(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(gameEntity: GameEntity)
}
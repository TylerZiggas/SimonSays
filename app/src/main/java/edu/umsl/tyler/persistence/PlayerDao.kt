package edu.umsl.tyler.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlayerDao {
    @Insert
    suspend fun addPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players ORDER BY score DESC LIMIT 10")
    suspend fun fetchPlayers(): List<PlayerEntity>
}
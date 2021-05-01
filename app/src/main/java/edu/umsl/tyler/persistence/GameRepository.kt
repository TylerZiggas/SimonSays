// Author: Tyler Ziggas
// Date: May 2021
// Our repository for calls

package edu.umsl.tyler.persistence

import android.app.Activity
import android.content.Context
import androidx.room.Room
import edu.umsl.tyler.game.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class GameRepository(private var ctx: Context) {

    private val db: GameDatabase

    init { // Initializing our database
        if (ctx is Activity) {
            ctx = ctx.applicationContext
        }
        db = Room.databaseBuilder(ctx, GameDatabase::class.java, "Scoreboard.sqlite").build()
    }

    fun saveScore(game: Game) { // If we are to save a score, take in a game data type
        val entity = GameEntity(game.difficulty, game.score, game.date ?: Date())
        CoroutineScope(Dispatchers.Default).launch {
            db.gameDao().insertScore(entity)
        }
    }

    fun fetchScores(): List<Game> { // Getting our entire scoreboard
        val result = runBlocking {
            return@runBlocking db.gameDao().fetchScoreboard()
        }

        return result.map {
            Game(it.difficulty, it.score, it.gameDate)
        }
    }
}
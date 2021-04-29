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

    init {
        if (ctx is Activity) {
            ctx = ctx.applicationContext
        }
        db = Room.databaseBuilder(ctx, GameDatabase::class.java, "Scoreboard.sqlite").build()
    }

    fun saveScore(game: Game) {
        val entity = GameEntity(game.difficulty, game.score, game.date ?: Date())
        CoroutineScope(Dispatchers.Default).launch {
            db.gameDao().insertScore(entity)
        }
    }

    fun fetchScores(): List<Game> {
        val result = runBlocking {
            return@runBlocking db.gameDao().fetchScoreboard()
        }

        return result.map {
            Game(it.difficulty, it.score, it.gameDate)
        }
    }
}
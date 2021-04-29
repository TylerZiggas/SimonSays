package edu.umsl.tyler.game

import androidx.lifecycle.ViewModel
import java.util.*

class GameModel: ViewModel() {
    lateinit var game: Game
        private set

    fun addNewGame(difficulty: String, score: Int) {
        game = Game(difficulty, score, Date())
    }

    fun getGameInfo(): Game? {
        return game
    }

    fun setGameScore(score: Int) {
        game.score = score
    }
}
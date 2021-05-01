// Author: Tyler Ziggas
// Date: May 2021
// view model for our game itself

package edu.umsl.tyler.game

import androidx.lifecycle.ViewModel
import java.util.*

class GameModel: ViewModel() {
    lateinit var game: Game
        private set

    fun addNewGame(difficulty: String, score: Int) { // Adding a new game
        game = Game(difficulty, score, Date())
    }

    fun getGameInfo(): Game? { // getting a games info
        return game
    }

    fun setGameScore(score: Int) { // setting our score
        game.score = score
    }
}
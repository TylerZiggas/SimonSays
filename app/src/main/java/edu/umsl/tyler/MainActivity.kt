// Author: Tyler Ziggas
// Date: May 2021
// This is the landing page and main activity for our simon says game.
// Here you can select a difficulty and it will make the game start with a different number of buttons and given you a different amount of time
// depending on your selection.

package edu.umsl.tyler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.umsl.tyler.game.GameModel
import edu.umsl.tyler.game.PlayGameActivity
import edu.umsl.tyler.persistence.GameRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var gameModel: GameModel
    private lateinit var difficultyString: String
    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameModel = ModelHolder.instance.get(GameModel::class) ?: GameModel()
        ModelHolder.instance.set(gameModel)

        easyBtn?.setOnClickListener { // Easy button selector
            difficultyString = "Easy"
            setCurrentDifficulty(difficultyString)
        }

        normalBtn?.setOnClickListener { // Normal button selector
            difficultyString = "Normal"
            setCurrentDifficulty(difficultyString)
        }

        hardBtn?.setOnClickListener { // Hard button selector
            difficultyString = "Hard"
            setCurrentDifficulty(difficultyString)
        }
    }

    private fun setCurrentDifficulty(difficultyString: String) { // Setting our difficulty
        if (!this::repository.isInitialized) { // Initialize our repository
            repository = GameRepository(this.applicationContext)
        }

        gameModel.addNewGame(difficultyString, 0) // Add new game to our view model
        ModelHolder.instance.set(gameModel)
        val intent = PlayGameActivity.newIntent(this, difficultyString, 0)
        startActivity(intent) // Start our game
    }
}
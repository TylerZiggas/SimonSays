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

        easyBtn?.setOnClickListener {
            difficultyString = "Easy"
            setCurrentDifficulty(difficultyString)
        }

        normalBtn?.setOnClickListener {
            difficultyString = "Normal"
            setCurrentDifficulty(difficultyString)
        }

        hardBtn?.setOnClickListener {
            difficultyString = "Hard"
            setCurrentDifficulty(difficultyString)
        }
    }

    private fun setCurrentDifficulty(difficultyString: String) {
        if (!this::repository.isInitialized) {
            repository = GameRepository(this.applicationContext)
        }

        gameModel.addNewGame(difficultyString, 0)
        ModelHolder.instance.set(gameModel)
        val intent = PlayGameActivity.newIntent(this, difficultyString, 0)
        startActivity(intent)
    }
}
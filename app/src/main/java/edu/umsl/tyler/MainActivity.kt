package edu.umsl.tyler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.umsl.tyler.game.GameModel
import edu.umsl.tyler.game.PlayGameActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var gameModel: GameModel
    private lateinit var receivedName: String
    private val easy = 1
    private val normal = 2
    private val hard = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameModel = ModelHolder.instance.get(GameModel::class) ?: GameModel()
        ModelHolder.instance.set(gameModel)

        easyBtn?.setOnClickListener {
            receivedName = "baby"
            gameModel.addNewPlayer(receivedName, easy)
            ModelHolder.instance.set(gameModel)
            val intent = PlayGameActivity.newIntent(this, receivedName, easy)
            startActivity(intent)
        }

        normalBtn?.setOnClickListener {
            receivedName = "person"
            gameModel.addNewPlayer(receivedName, normal)
            ModelHolder.instance.set(gameModel)
            val intent = PlayGameActivity.newIntent(this, receivedName, normal)
            startActivity(intent)
        }

        hardBtn?.setOnClickListener {
            receivedName = "god"
            gameModel.addNewPlayer(receivedName, hard)
            ModelHolder.instance.set(gameModel)
            val intent = PlayGameActivity.newIntent(this, receivedName, hard)
            startActivity(intent)
        }
    }
}
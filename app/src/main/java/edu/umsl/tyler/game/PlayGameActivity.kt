package edu.umsl.tyler.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import edu.umsl.tyler.R

class PlayGameActivity : AppCompatActivity() {

    companion object {
        private const val PASSING_PLAYER_NAME = "edu.umsl.tyler.PLAYER_NAME"
        private const val PASSING_GAME_LEVEL = "edu.umsl.tyler.LEVEL"
        @JvmStatic
        fun newIntent(context: FragmentActivity?, name: String, lvl: Int): Intent {
            val intent = Intent(context, PlayGameActivity::class.java)
            intent.putExtra(PASSING_PLAYER_NAME, name)
            intent.putExtra(PASSING_GAME_LEVEL, lvl)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val playGameFragment = PlayGameFragment()

        if (savedInstanceState == null) {
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragmentContainer, playGameFragment)
            transaction.commit()
        }
    }
}
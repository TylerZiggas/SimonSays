// Author: Tyler Ziggas
// Date: May 2021
// Empty activity for our game

package edu.umsl.tyler.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import edu.umsl.tyler.R
import edu.umsl.tyler.persistence.GameRepository

class PlayGameActivity : AppCompatActivity() {

    private lateinit var repository: GameRepository

    companion object {
        @JvmStatic
        fun newIntent(context: FragmentActivity?, difficulty: String, score: Int): Intent { // On start we want to put in our extras
            val intent = Intent(context, PlayGameActivity::class.java)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("score", score)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        if (!this::repository.isInitialized) { // Initialize repository just in case
            repository = GameRepository(this.applicationContext)
        }

        val playGameFragment = PlayGameFragment()

        if (savedInstanceState == null) { // Start our fragment
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragmentContainer, playGameFragment)
            transaction.commit()
        }
    }
}
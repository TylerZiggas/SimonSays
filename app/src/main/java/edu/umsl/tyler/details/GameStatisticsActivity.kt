// Author: Tyler Ziggas
// Date: May 2021
// Activity for our game statistics/results screen, used to display our difficulty level and score on this run

package edu.umsl.tyler.details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import edu.umsl.tyler.R
import kotlinx.android.synthetic.main.activity_dashboard.*

class GameStatisticsActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun newIntent(context: FragmentActivity?): Intent {
            return Intent(context, GameStatisticsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val yourDifficulty = intent.getSerializableExtra("difficulty") // Grab our difficulty and score from the intent
        val yourScore = intent.getSerializableExtra("score")

        val difficultyLevel = when (yourDifficulty) { // Check what difficulty was played just now
            1 -> {
                "Easy"
            }
            2 -> {
                "Normal"
            }
            else -> {
                "Hard"
            }
        }

        val difficultyText = "Difficulty: $difficultyLevel" // Display difficulty and score
        val scoreText = "Score: " + yourScore.toString()
        currentDifficulty.text = difficultyText
        currentScore.text = scoreText

        replayButton.setOnClickListener { // On replay we just end this activity
            finish()
        }

        val gameStatsFragment = GameStatisticsFragment()

        if (savedInstanceState == null) { // Start fragment
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.add(R.id.dashFragmentContainer, gameStatsFragment)
            transaction.commit()
        }
    }
}
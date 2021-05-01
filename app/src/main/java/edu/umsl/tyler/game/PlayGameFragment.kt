// Author: Tyler Ziggas
// Date: May 2021
// This is the fragment where the game is played

package edu.umsl.tyler.game

import android.animation.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.umsl.tyler.ModelHolder
import edu.umsl.tyler.R
import edu.umsl.tyler.details.GameStatisticsActivity
import edu.umsl.tyler.persistence.GameRepository
import kotlinx.android.synthetic.main.fragment_play_game.*
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

class PlayGameFragment: Fragment() {

    private lateinit var gamerModel: GameModel
    private lateinit var viewModel: FlashSequenceModel
    private lateinit var repository: GameRepository
    private val buttonColors = ArrayList<Button>()
    var totalDuration = 0
    private var difficultyLevel: Int = 0
    private var currentScore: Int = 0
    private var position: Int = 0


    private val easyTimer = object: CountDownTimer(3000, 1000) { // Timer for easy mode
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() { // We want to flash correct button and end
            flashButton(viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    private val normalTimer = object: CountDownTimer(2000, 1000) { // Timer for normal mode
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() { // We want to flash correct button and end
            flashButton(viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    private val hardTimer = object: CountDownTimer(1000, 1000) { // Timer for hard mode
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() { // We want to flash correct button and end
            flashButton(viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gamerModel = ModelHolder.instance.get(GameModel::class)!!
        difficultyLevel = when (gamerModel.getGameInfo()!!.difficulty) { // Figure out which difficulty was selected
            "Easy" -> {
                1
            }
            "Normal" -> {
                2
            }
            else -> {
                3
            }
        }
        viewModel = ViewModelProvider(this).get(FlashSequenceModel::class.java)
        return inflater.inflate(R.layout.fragment_play_game, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance

        buttonColors.add(yellowBtn) // Add the different options of colors
        buttonColors.add(blueBtn)
        buttonColors.add(greenBtn)
        buttonColors.add(redBtn)

        if (savedInstanceState == null) { // Create a new sequence of buttons
            initialRandomSequence(buttonColors)
        }
        viewModel.resetTracker()

        controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, false) // Do not let user click buttons while we show sequence
        runUIUpdate()
        controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, true)

        yellowBtn.setOnClickListener(selectionListener) // Set our on click listeners for our buttons
        blueBtn.setOnClickListener(selectionListener)
        greenBtn.setOnClickListener(selectionListener)
        redBtn.setOnClickListener(selectionListener)
        restartBtn.setOnClickListener(gameOverSelection)
        scoreBoard.setOnClickListener(gameOverSelection)

    }

    private fun difficultyTimerStart() { // Starting our timer based on our difficulty
        when (difficultyLevel) {
            1 -> {
                easyTimer.start()
            }
            2 -> {
                normalTimer.start()
            }
            else -> {
                hardTimer.start()
            }
        }
    }

    private fun difficultyTimerEnd() { // Ending our timer based on our difficulty
        when (difficultyLevel) {
            1 -> {
                easyTimer.cancel()
            }
            2 -> {
                normalTimer.cancel()
            }
            else -> {
                hardTimer.cancel()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private var gameOverSelection = View.OnClickListener { v -> // Our selection of buttons after a game over
        when(v.id){
            R.id.restartBtn -> { // Restart game if they want to restart
                restartGame()
            }
            R.id.scoreBoard -> { // Go to scoreboard if we are finished with the game
                viewModel.clearSeq()
                val intent = GameStatisticsActivity.newIntent(activity) // Grab intent and pass this runs difficulty and score to display
                intent.putExtra("difficulty", difficultyLevel)
                intent.putExtra("score", currentScore)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private var selectionListener = View.OnClickListener { v -> // Listener for checking the button sequence

        var validateSeq = true
        when(v.id){
            R.id.redBtn -> { // Check if red is correct
                addGameNewSequence(redBtn)
                validateSeq = checkPlayerChoice(redBtn)
            }
            R.id.greenBtn -> { // Check if green is correct
                addGameNewSequence(greenBtn)
                validateSeq = checkPlayerChoice(greenBtn)
            }
            R.id.blueBtn -> { // Check if blue is correct
                addGameNewSequence(blueBtn)
                validateSeq = checkPlayerChoice(blueBtn)
            }
            R.id.yellowBtn -> { // Check if yellow is correct
                addGameNewSequence(yellowBtn)
                validateSeq = checkPlayerChoice(yellowBtn)
            }
        }
        if (validateSeq) { // Check to see if the sequences are now the same
            if (viewModel.getGameSeq().size === viewModel.getSeq().size) {
                currentScore++
                gamerModel.setGameScore(currentScore)
                val newScore = "Score: " + gamerModel.getGameInfo()?.score
                scoreText.text = newScore

                difficultyTimerEnd()
                addNewRandomSequence(buttonColors) // Add a new button to the sequence
                controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, false)
                runUIUpdate() // Show the new sequence
                controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun restartGame() { // Clear everything and restart our game
        restartBtn?.visibility = View.INVISIBLE // Reset game over screen to invisible
        scoreBoard?.visibility = View.INVISIBLE
        gameOverText?.visibility = View.INVISIBLE
        viewModel.clearSeq() // Clear sequences
        viewModel.clearGameSeq()
        totalDuration = 0
        currentScore = 0
        position = 0
        scoreText.text = "Score: 0"
        initialRandomSequence(buttonColors) // Create our new sequence
        controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, false)
        runUIUpdate()
        controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPlayerChoice(btn: Button): Boolean { // Check our choice
            if (btn !== viewModel.getSeq()[position]) { // Compare the position in the sequence to the button we selected
                flashButton(viewModel.getSeq()[position], false) // Flash what you should have clicked
                gameOver()  // End game
                controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, false)
                difficultyTimerEnd()
                position++
                return false
            } else {
                flashButton(viewModel.getSeq()[position], true) // FLash what you clicked that was correct
                difficultyTimerStart() // Reset timer after each correct click
                position++
            }
        return true
    }

    fun gameOver() {
        controlClickability(yellowBtn, blueBtn, greenBtn, redBtn, false) // Show the game over screen and disable buttons
        scoreText?.text = "GAME OVER"
        restartBtn?.visibility = View.VISIBLE
        scoreBoard?.visibility = View.VISIBLE
        gameOverText?.visibility = View.VISIBLE

        if (!this::repository.isInitialized) { // Initialize repository before inserting info
            repository = activity?.let { GameRepository(it) }!!
        }

        runBlocking { // Inserting our game data
            val gameData = (gamerModel.getGameInfo())
            context?.let {
                if (gameData != null) {
                    repository.saveScore(gameData)
                }
            }
        }
    }

    private fun addGameNewSequence(pressedButton: Button) { // Call to add a new sequence to our buttons
        viewModel.addGameSeq(pressedButton)
    }

    private fun initialRandomSequence(allBtnColors: ArrayList<Button>) { // Our initial sequence changes upon our difficulty
        when (difficultyLevel) {
            1 -> {
                val random = (0..3).random()
                viewModel.addSeq(allBtnColors[random])
            }
            2 -> {
                for(i in 1..3) {
                    val random = (0..3).random()
                    viewModel.addSeq(allBtnColors[random])
                }
            }
            else -> {
                for (i in 1..5) {
                    val random = (0..3).random()
                    viewModel.addSeq(allBtnColors[random])
                }
            }
        }
    }

    private fun addNewRandomSequence(allBtnColors: ArrayList<Button>) { // Random add for the next button
        val random = (0..3).random()
        viewModel.addSeq(allBtnColors[random])
    }

    private fun controlClickability(btn1: Button, btn2: Button, btn3: Button, btn4: Button, value: Boolean) { // Changing our whether it should be clickable or not
        if (!value) {
            btn1.isEnabled = value
            btn2.isEnabled = value
            btn3.isEnabled = value
            btn4.isEnabled = value
        }
        else {
            val handler = Handler()
            val runnable = Runnable { // Setting our runnable for clicking the correct sequence
                btn1.isEnabled = value
                btn2.isEnabled = value
                btn3.isEnabled = value
                btn4.isEnabled = value

                difficultyTimerStart()
            }
            handler.postDelayed(runnable, totalDuration.toLong())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun flashButton(btn: Button, validation: Boolean) { // Function for flashing a single button
        activity?.let {activity ->
            val originalColor = btn.background as? ColorDrawable
            val redColor = if (validation) { // If we are correct flash it green, wrong or time is out flash red
                ContextCompat.getColor(activity, R.color.rightAccent)
            } else {
                ContextCompat.getColor(activity, R.color.wrongAccent)
            }
            val animator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor?.color, redColor, originalColor?.color)

            animator.addUpdateListener { valueAnimator ->
                (valueAnimator.animatedValue as? Int)?.let { animatedValue -> btn.setBackgroundColor(animatedValue)
                }
            }

            animator?.start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun runUIUpdate() { // Show the entire sequence
        position = 0
        var index = 0
        activity?.let {activity ->
            viewModel.clearGameSeq() // Clear our old sequence that was shown
            for (btn in viewModel.sequenceTracker) {
                val originalColor = btn.background as? ColorDrawable
                val redColor = ContextCompat.getColor(activity, R.color.colorAccent) // Set up colors
                val animator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor?.color, redColor, originalColor?.color)

                animator.addUpdateListener { valueAnimator ->
                    (valueAnimator.animatedValue as? Int)?.let { animatedValue -> btn.setBackgroundColor(animatedValue)
                    }
                }

                animator?.startDelay = ((index+1) * (2000/3)).toLong() // Set up how long the animation plays
                animator?.duration = 2000.toLong()/3
                totalDuration = animator.totalDuration.toInt()
                animator?.start()

                animator.addListener(object : AnimatorListenerAdapter() { // Animate said button
                    override fun onAnimationEnd(animation: Animator) {
                        viewModel.popButton()
                    }
                })
                index++
            }
        }
    }

    override fun onDestroy() { // On destroy cancel timers in case of problem and clear view model
        super.onDestroy()
        easyTimer.cancel()
        normalTimer.cancel()
        hardTimer.cancel()
        viewModel.clearGameSeq()
        viewModel.resetTracker()
    }

}
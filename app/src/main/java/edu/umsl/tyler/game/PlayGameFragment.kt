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


    private val easyTimer = object: CountDownTimer(3000, 1000) {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() {
            flashButton( viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    private val normalTimer = object: CountDownTimer(2000, 1000) {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() {
            flashButton( viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    private val hardTimer = object: CountDownTimer(1000, 1000) {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onFinish() {
            flashButton( viewModel.getSeq()[position], false)
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            val newText = "Timer: " + millisUntilFinished / 1000
            timerText?.text = newText
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gamerModel = ModelHolder.instance.get(GameModel::class)!!
        difficultyLevel = when (gamerModel.getGameInfo()!!.difficulty) {
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

        buttonColors.add(yellowBtn)
        buttonColors.add(blueBtn)
        buttonColors.add(greenBtn)
        buttonColors.add(redBtn)

        if (savedInstanceState == null){
            initialRandomSequence(buttonColors)
        }
        viewModel.resetTracker()

        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
        runUIUpdate()
        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, true)

        yellowBtn.setOnClickListener(selectionListener)
        blueBtn.setOnClickListener(selectionListener)
        greenBtn.setOnClickListener(selectionListener)
        redBtn.setOnClickListener(selectionListener)
        restartBtn.setOnClickListener(gameOverSelection)
        scoreBoard.setOnClickListener(gameOverSelection)

    }

    private fun difficultyTimerStart() {
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

    private fun difficultyTimerEnd() {
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
    private var gameOverSelection = View.OnClickListener { v ->
        when(v.id){
            R.id.restartBtn -> {
                restartGame()
            }
            R.id.scoreBoard -> {
                viewModel.clearSeq()
                val intent = GameStatisticsActivity.newIntent(activity)
                intent.putExtra("difficulty", difficultyLevel)
                intent.putExtra("score", currentScore)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private var selectionListener = View.OnClickListener { v ->

        var validateSeq = true
        when(v.id){
            R.id.redBtn -> {
                addGameNewSequence(redBtn)
                validateSeq = checkPlayerChoice(redBtn)
            }
            R.id.greenBtn -> {
                addGameNewSequence(greenBtn)
                validateSeq = checkPlayerChoice(greenBtn)
            }
            R.id.blueBtn -> {
                addGameNewSequence(blueBtn)
                validateSeq = checkPlayerChoice(blueBtn)
            }
            R.id.yellowBtn -> {
                addGameNewSequence(yellowBtn)
                validateSeq = checkPlayerChoice(yellowBtn)
            }
        }
        if (validateSeq) {
            if (viewModel.getGameSeq().size === viewModel.getSeq().size) {
                currentScore++
                gamerModel.setGameScore(currentScore)
                val newScore = "Score: " + gamerModel.getGameInfo()?.score
                scoreText.text = newScore


                difficultyTimerEnd()
                addNewRandomSequence(buttonColors)
                controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
                runUIUpdate()
                controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun restartGame() {
        restartBtn?.visibility = View.INVISIBLE
        scoreBoard?.visibility = View.INVISIBLE
        gameOverText?.visibility = View.INVISIBLE
        viewModel.clearSeq()
        viewModel.clearGameSeq()
        totalDuration = 0
        currentScore = 0
        position = 0
        scoreText.text = "Score: 0"
        initialRandomSequence(buttonColors)
        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
        runUIUpdate()
        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPlayerChoice(btn: Button): Boolean {
            if (btn !== viewModel.getSeq()[position]) {
                flashButton(viewModel.getSeq()[position], false)
                gameOver()
                controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
                difficultyTimerEnd()
                position++
                return false
            } else {
                flashButton(viewModel.getSeq()[position], true)
                difficultyTimerStart()
                position++
            }
        return true
    }

    fun gameOver() {
        scoreText?.text = "GAME OVER"
        restartBtn?.visibility = View.VISIBLE
        scoreBoard?.visibility = View.VISIBLE
        gameOverText?.visibility = View.VISIBLE

        if (!this::repository.isInitialized) {
            repository = activity?.let { GameRepository(it) }!!
        }

        runBlocking {
            val gameData = (gamerModel.getGameInfo())
            context?.let {
                if (gameData != null) {
                    repository.saveScore(gameData)
                }
            }
        }
    }

    private fun addGameNewSequence(pressedButton: Button) {
        viewModel.addGameSeq(pressedButton)
    }

    private fun initialRandomSequence(allBtnColors: ArrayList<Button>) {
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

    private fun addNewRandomSequence(allBtnColors: ArrayList<Button>) {
        val random = (0..3).random()
        viewModel.addSeq(allBtnColors[random])
    }

    private fun controlButtonsVisibility(btn1: Button, btn2: Button, btn3: Button, btn4: Button, value: Boolean) {
        if (!value) {
            btn1.isEnabled = value
            btn2.isEnabled = value
            btn3.isEnabled = value
            btn4.isEnabled = value
        }
        else {
            val handler = Handler()
            val runnable = Runnable {
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
    fun flashButton(btn: Button, validation: Boolean) {
        activity?.let {activity ->
            val originalColor = btn.background as? ColorDrawable
            val redColor = if (validation) {
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
    fun runUIUpdate() {
        position = 0
        var index = 0
        activity?.let {activity ->
            viewModel.clearGameSeq()
            for (btn in viewModel.sequenceTracker) {
                val originalColor = btn.background as? ColorDrawable
                val redColor = ContextCompat.getColor(activity, R.color.colorAccent)
                val animator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor?.color, redColor, originalColor?.color)

                animator.addUpdateListener { valueAnimator ->
                    (valueAnimator.animatedValue as? Int)?.let { animatedValue -> btn.setBackgroundColor(animatedValue)
                    }
                }

                animator?.startDelay = ((index+1) * (2000/difficultyLevel)).toLong()
                animator?.duration = 2000.toLong()/difficultyLevel
                totalDuration = animator.totalDuration.toInt()
                animator?.start()

                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        viewModel.popButton()
                    }
                })
                index++
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearGameSeq()
    }

}
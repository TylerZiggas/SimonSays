package edu.umsl.tyler.game

import android.animation.*
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
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
import edu.umsl.tyler.persistence.SimonDB
import edu.umsl.tyler.details.GameStatisticsActivity
import edu.umsl.tyler.persistence.PlayerEntity
import kotlinx.android.synthetic.main.fragment_play_game.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayGameFragment: Fragment() {

    private lateinit var gamerModel: GameModel
    private lateinit var viewModel: FlashSequenceModel
    private val buttonColors = ArrayList<Button>()
    var totalDuration = 0
    private lateinit var playerName: String
    private var difficultyLevel: Int = 0
    private var currentScore: Int = 0


    private val easyTimer = object: CountDownTimer(3000, 1000) {
        override fun onFinish() {
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            timerText?.text = "Timer: " + millisUntilFinished / 1000
        }
    }

    private val normalTimer = object: CountDownTimer(2000, 1000) {
        override fun onFinish() {
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            timerText?.text = "Timer: " + millisUntilFinished / 1000
        }
    }

    private val hardTimer = object: CountDownTimer(1000, 1000) {
        override fun onFinish() {
            gameOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            timerText?.text = "Timer: " + millisUntilFinished / 1000
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        gamerModel = ModelHolder.instance.get(GameModel::class)!!
        playerName = gamerModel.getPlayerInfo()!!.name
        difficultyLevel = gamerModel.getPlayerInfo()!!.level
        viewModel = ViewModelProvider(this).get(FlashSequenceModel::class.java)
        val view = inflater.inflate(R.layout.fragment_play_game, container, false)
        return view
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
        Log.e("TAG-viewModel", viewModel.sequence.toString())
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

    @RequiresApi(Build.VERSION_CODES.N)
    private var gameOverSelection = View.OnClickListener { v ->
        when(v.id){
            R.id.restartBtn -> {
                restartGame()
            }
            R.id.scoreBoard -> {
                viewModel.clearSeq()
                val intent = GameStatisticsActivity.newIntent(activity)
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
                addPlayerNewSequence(redBtn)
                validateSeq = checkPlayerChoice()
            }
            R.id.greenBtn -> {
                addPlayerNewSequence(greenBtn)
                validateSeq = checkPlayerChoice()
            }
            R.id.blueBtn -> {
                addPlayerNewSequence(blueBtn)
                validateSeq = checkPlayerChoice()
            }
            R.id.yellowBtn -> {
                addPlayerNewSequence(yellowBtn)
                validateSeq = checkPlayerChoice()
            }
        }
        if (validateSeq) {
            if (viewModel.getPlayerSeq().size === viewModel.getSeq().size) {
                currentScore++
                gamerModel.setPlayerScore(currentScore)
                scoreText.text = "Score: " + gamerModel.getPlayerInfo()?.score


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
        viewModel.clearPlayerSeq()
        totalDuration = 0
        currentScore = 0
        scoreText.text = "Score: 0"
        initialRandomSequence(buttonColors)
        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
        runUIUpdate()
        controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPlayerChoice(): Boolean {
        for (position in 0 until viewModel.getPlayerSeq().size) {
            if (viewModel.getPlayerSeq()[position] !== viewModel.getSeq()[position]) {
                gameOver()
                controlButtonsVisibility(yellowBtn, blueBtn, greenBtn, redBtn, false)
                difficultyTimerEnd()
                return false
            } else {
                difficultyTimerStart()
            }
        }
        return true
    }

    fun gameOver() {
        scoreText?.text = "GAME OVER"
        restartBtn?.visibility = View.VISIBLE
        scoreBoard?.visibility = View.VISIBLE
        gameOverText?.visibility = View.VISIBLE

        runBlocking {
            val playerData = PlayerEntity(gamerModel.getPlayerInfo()?.name!!, gamerModel.getPlayerInfo()?.score!!)
            context?.let {
                SimonDB(it).getPlayerDataDao().addPlayer(playerData)
            }
        }
    }

    private fun addPlayerNewSequence(pressedButton: Button) {
        viewModel.addPlayerSeq(pressedButton)
    }

    private fun initialRandomSequence(allBtnColors: ArrayList<Button>) {
        when (difficultyLevel) {
            1 -> {
                var random = (0..3).random()
                viewModel.addSeq(allBtnColors[random])
            }
            2 -> {
                for(i in 1..3) {
                    var random = (0..3).random()
                    viewModel.addSeq(allBtnColors[random])
                }
            }
            else -> {
                for (i in 1..5) {
                    var random = (0..3).random()
                    viewModel.addSeq(allBtnColors[random])
                }
            }
        }
    }

    private fun addNewRandomSequence(allBtnColors: ArrayList<Button>) {
        var random = (0..3).random()
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
    fun runUIUpdate() {
        var index = 0
        activity?.let {activity ->
            viewModel.clearPlayerSeq()
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
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                    }
                    override fun onAnimationEnd(animation: Animator) {
                        viewModel.popButton()
                    }
                })
                index++
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearPlayerSeq()
    }

}
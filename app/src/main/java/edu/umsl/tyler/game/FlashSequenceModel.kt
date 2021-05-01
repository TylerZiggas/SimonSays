// Author: Tyler Ziggas
// Date: May 2021
// View model for our sequences

package edu.umsl.tyler.game

import android.widget.Button
import androidx.lifecycle.ViewModel

class FlashSequenceModel: ViewModel() {

    var sequence: ArrayList<Button> = ArrayList()
        private set

    var gameSequence: ArrayList<Button> = ArrayList()
        private set

    var sequenceTracker: ArrayList<Button> = ArrayList()
        private set

    fun addSeq(btn: Button) { // Adding a button to the sequence
        sequence.add(btn)
        resetTracker()
    }

    fun addGameSeq(btn: Button) { // adding a button to the game sequence
        gameSequence.add(btn)
    }

    fun getSeq(): ArrayList<Button> { // get our sequence
        return sequence
    }

    fun getGameSeq(): ArrayList<Button> { // getting our game sequence
        return gameSequence
    }

    fun popButton() { // Popping a button in our sequence tracker
        if (sequenceTracker.isNotEmpty()) {
            sequenceTracker.removeAt(0)
        }
    }

    fun clearSeq() { // clearing our sequence
        sequence.clear()
    }

    fun clearGameSeq() { // clearing our game sequence
        gameSequence.clear()
    }

    fun resetTracker() { // clearing our tracker
        sequenceTracker.clear()
        sequenceTracker.addAll(sequence)
    }
}
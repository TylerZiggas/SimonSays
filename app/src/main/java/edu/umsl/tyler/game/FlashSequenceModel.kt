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

    fun addSeq(btn: Button){
        sequence.add(btn)
        resetTracker()
    }

    fun addGameSeq(btn: Button) {
        gameSequence.add(btn)
    }

    fun getSeq(): ArrayList<Button> {
        return sequence
    }

    fun getGameSeq(): ArrayList<Button> {
        return gameSequence
    }

    fun popButton() {
        if (sequenceTracker.isNotEmpty()) {
            sequenceTracker.removeAt(0)
        }
    }

    fun clearSeq() {
        sequence.clear()
    }

    fun clearGameSeq() {
        gameSequence.clear()
    }

    fun resetTracker() {
        sequenceTracker.clear()
        sequenceTracker.addAll(sequence)
    }
}
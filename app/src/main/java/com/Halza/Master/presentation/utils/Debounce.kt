package com.Halza.Master.presentation.utils

import java.util.Timer
import java.util.TimerTask


class Debounce(private val delayInMillisecond: Long) {
    private var timer :Timer? = null
    private var timerTask: ActionTimerTask? = null

    fun doAction(action: () -> Unit): Unit {
        // Stop Current Task
        timerTask?.cancel()
        timer?.cancel()

        // Start new task
        timer = Timer()
        timerTask = ActionTimerTask(action)
        timer?.schedule(timerTask, delayInMillisecond)
    }
}

internal class ActionTimerTask(private val action: () -> Unit) : TimerTask() {
    override fun run() {
        action()
    }
}
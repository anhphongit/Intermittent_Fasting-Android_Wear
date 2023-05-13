package com.Halza.Master.presentation.utils

import java.util.Timer
import java.util.TimerTask


class Debounce(private val delayInMillisecond: Long) {
    private val timer = Timer()
    private var timerTask: ActionTimerTask? = null

    fun doAction(action: () -> Unit): Unit {
        timerTask?.cancel();
        timerTask = ActionTimerTask(action)
        timer.schedule(timerTask, delayInMillisecond)
    }
}

internal class ActionTimerTask(private val action: () -> Unit) : TimerTask() {
    override fun run() {
        action()
    }
}
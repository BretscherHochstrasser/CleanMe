package ch.bretscherhochstrasser.cleanme

import java.util.*

/**
 * A repeating trigger that can be started/stopped.
 */
class RepeatingUpdater(private val onUpdate: () -> Unit, private val interval: Long) {

    private var started = false
    private lateinit var timer: Timer

    fun start() {
        if (!started) {
            val triggerTask = object : TimerTask() {
                override fun run() {
                    onUpdate()
                }
            }
            timer = Timer()
            timer.schedule(triggerTask, interval, interval)
            started = true
        }
    }

    fun stop() {
        if (started) {
           timer.cancel()
        }
    }

}

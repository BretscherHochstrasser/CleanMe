package ch.bretscherhochstrasser.cleanme

import timber.log.Timber
import java.util.*

/**
 * A repeating trigger that can be started/stopped.
 */
class RepeatingUpdater(private val onUpdate: () -> Unit, private val interval: Long) {

    private var started = false
    private lateinit var timer: Timer

    fun start() {
        if (!started) {
            Timber.d("Starting repeating updater")
            val callbackTask = object : TimerTask() {
                override fun run() {
                    Timber.d("Repeating updater triggered")
                    onUpdate()
                }
            }
            timer = Timer()
            timer.schedule(callbackTask, interval, interval)
            started = true
        }
    }

    fun stop() {
        if (started) {
            Timber.d("Stopping repeating updater")
            timer.cancel()
            started = false
        }
    }

}

package ch.bretscherhochstrasser.cleanme.helper

import android.os.Handler
import android.os.Looper
import timber.log.Timber

/**
 * A repeating updater based on a [Handler] that can be started/stopped and always triggers
 * the [onUpdate] function on the UI thread.
 */
class RepeatingUpdater(private val onUpdate: () -> Unit, private val interval: Long) {

    private var started = false
    private var handler = Handler(Looper.getMainLooper())
    private var callback = Runnable(::onCallback)

    fun start() {
        if (!started) {
            Timber.d("Starting repeating updater")
            handler.postDelayed(callback, interval)
            started = true
        }
    }

    private fun onCallback() {
        Timber.d("Update triggered")
        onUpdate()
        handler.postDelayed(callback, interval)
    }

    fun stop() {
        if (started) {
            Timber.d("Stopping repeating updater")
            handler.removeCallbacks(callback)
            started = false
        }
    }

}

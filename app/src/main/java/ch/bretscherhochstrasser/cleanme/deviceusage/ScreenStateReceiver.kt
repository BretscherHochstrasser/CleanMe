package ch.bretscherhochstrasser.cleanme.deviceusage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import timber.log.Timber

/**
 * Receiver for the ACTION_SCREEN_ON / ACTION_SCREEN_OFF broadcasts
 */
class ScreenStateReceiver(
    private val onScreenOn: () -> Unit,
    val onScreenOff: () -> Unit
) : BroadcastReceiver() {

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        context.registerReceiver(this, intentFilter)
        Timber.d("ScreenStateReceiver registered")
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
        Timber.d("ScreenStateReceiver unregistered")
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> screenOnReceived()
            Intent.ACTION_SCREEN_OFF -> screenOffReceived()
        }
    }

    private fun screenOffReceived() {
        Timber.d("Received SCREEN_OFF broadcast")
        onScreenOff()
    }

    private fun screenOnReceived() {
        Timber.d("Received SCREEN_ON broadcast")
        onScreenOn()
    }
}

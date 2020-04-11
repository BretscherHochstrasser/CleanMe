package ch.bretscherhochstrasser.cleanme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenStateReceiver(
    private val onScreenOn: () -> Unit,
    val onScreenOff: () -> Unit) : BroadcastReceiver() {

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        context.registerReceiver(this, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> screenOnReceived()
            Intent.ACTION_SCREEN_OFF -> screenOffReceived()
        }
    }

    private fun screenOffReceived() {
        onScreenOff()
    }

    private fun screenOnReceived() {
        onScreenOn()
    }
}

package ch.bretscherhochstrasser.cleanme.service

import android.content.Context
import android.content.Intent
import android.os.Build
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import toothpick.InjectConstructor
import javax.inject.Singleton

/**
 * Holds the current state of the [CleanMeService], provides utility functions to invoke commands
 * on the service: [startObserveDeviceUsage], [stopObserveDeviceUsage].
 */
@Singleton
@InjectConstructor
class ServiceHelper(@AppContext private val context: Context) {

    fun startObserveDeviceUsage() {
        startForegroundWithAction(CleanMeService.ACTION_START)
    }

    fun stopObserveDeviceUsage() {
        startForegroundWithAction(CleanMeService.ACTION_STOP)
    }

    private fun startForegroundWithAction(action: String) {
        val startIntent = getIntent(action)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startIntent)
        } else {
            context.startService(startIntent)
        }
    }

    private fun getIntent(action: String): Intent {
        val intent = Intent(context, CleanMeService::class.java)
        intent.action = action
        return intent
    }

}

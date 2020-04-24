package ch.bretscherhochstrasser.cleanme.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import toothpick.InjectConstructor
import javax.inject.Singleton

/**
 * Holds the current state of the [CleanMeService], provides utility functions to invoke commands
 * on the service: [startObserveDeviceUsage], [stopObserveDeviceUsage], [refresh] and provide pending intent for notification actions
 */
@Singleton
@InjectConstructor
class ServiceHelper(@AppContext private val context: Context) {

    companion object {
        private const val REQUEST_CODE_SHOW_OVERLAY = 10
        private const val REQUEST_CODE_HIDE_OVERLAY = 20
    }

    fun startObserveDeviceUsage() {
        startWithAction(CleanMeService.ACTION_START)
    }

    fun stopObserveDeviceUsage() {
        startWithAction(CleanMeService.ACTION_STOP)
    }

    fun refresh() {
        startWithAction(CleanMeService.ACTION_REFRESH)
    }

    private fun startWithAction(action: String) {
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

    val showOverlayPendingIntent: PendingIntent?
        get() = getPendingIntent(
            REQUEST_CODE_SHOW_OVERLAY,
            getIntent(CleanMeService.ACTION_SHOW_OVERLAY)
        )

    val hideOverlayPendingIntent: PendingIntent?
        get() = getPendingIntent(
            REQUEST_CODE_HIDE_OVERLAY,
            getIntent(CleanMeService.ACTION_HIDE_OVERLAY)
        )

    private fun getPendingIntent(
        requestCode: Int,
        intent: Intent
    ): PendingIntent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getService(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

}

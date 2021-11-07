package ch.bretscherhochstrasser.cleanme.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStatsManager
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

/**
 * Broadcast receiver to handle notification actions
 */
class NotificationActionReceiver : BroadcastReceiver() {

    private val settings: AppSettings by inject()
    private val usageStatsManager: DeviceUsageStatsManager by inject()

    companion object {
        private const val ACTION_SHOW_OVERLAY = "SHOW_OVERLAY"
        private const val ACTION_HIDE_OVERLAY = "HIDE_OVERLAY"

        fun getShowOverlayPendingIntent(context: Context): PendingIntent? {
            return getPendingIntent(
                ACTION_SHOW_OVERLAY,
                context
            )
        }

        fun hideOverlayPendingIntent(context: Context): PendingIntent? {
            return getPendingIntent(
                ACTION_HIDE_OVERLAY,
                context
            )
        }

        private fun getPendingIntent(action: String, context: Context): PendingIntent? {
            val intent = Intent(context, NotificationActionReceiver::class.java)
            intent.action = action
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
            return PendingIntent.getBroadcast(context, 0, intent, flags)
        }

    }

    override fun onReceive(context: Context, intent: Intent) {
        KTP.openScopes(ApplicationScope::class.java).inject(this)
        when (intent.action) {
            ACTION_SHOW_OVERLAY -> setOverlayEnabled(true)
            ACTION_HIDE_OVERLAY -> setOverlayEnabled(false)
        }
    }

    private fun setOverlayEnabled(enableOverlay: Boolean) {
        settings.overlayEnabled = enableOverlay
        usageStatsManager.updateUsageStats() //trigger UI update
    }
}

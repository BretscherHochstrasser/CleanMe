package ch.bretscherhochstrasser.cleanme.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import timber.log.Timber
import toothpick.InjectConstructor

/**
 * Handles creation and updates to notifications
 */
@InjectConstructor
class NotificationHelper(private val context: Context) {

    companion object {
        const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "service_notification"
    }

    fun createNotification(deviceUsageStats: DeviceUsageStats): Notification {
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Counting screen on time")
            .setContentText(
                String.format(
                    "Screen on count: %d, total %s", deviceUsageStats.screenOnCount,
                    formatHoursAndMinutes(deviceUsageStats.deviceUseDuration)
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
        return builder.build()
    }

    fun updateNotification(deviceUsageStats: DeviceUsageStats) {
        Timber.d("Updating notification")
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, createNotification(deviceUsageStats))
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Screen state observer service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            Timber.d(
                "Notification channel '%s' created",
                CHANNEL_ID
            )
        }
    }

}
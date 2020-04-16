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
import org.threeten.bp.Duration
import timber.log.Timber

/**
 * Handles creation and updates to notifications
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "service_notification"
    }

    fun createNotification(deviceUsageStats: DeviceUsageStats): Notification {
        val screenOnDuration = Duration.ofMillis(deviceUsageStats.deviceUseDuration)
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Counting screen on time")
            .setContentText(
                String.format(
                    "Screen on count: %d, total %02d:%02d", deviceUsageStats.screenOnCount,
                    screenOnDuration.toHours(), screenOnDuration.toMinutes() % 60
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
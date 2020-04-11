package ch.bretscherhochstrasser.cleanme

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.threeten.bp.Duration

/**
 * Handles creation and updates to notifications
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "service_notification"
    }

    private val usageStats = DeviceUsageStats(context)

    fun createNotification(): Notification {
        val screenOnDuration = Duration.ofMillis(usageStats.deviceUseDuration)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Counting screen on time")
            .setContentText(
                String.format(
                    "Screen on count: %d, total %02d:%02d", usageStats.screenOnCount,
                    screenOnDuration.toHours(), screenOnDuration.toMinutes() % 60
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
        return builder.build()
    }

    fun updateNotification() {
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, createNotification())
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
        }
    }

}
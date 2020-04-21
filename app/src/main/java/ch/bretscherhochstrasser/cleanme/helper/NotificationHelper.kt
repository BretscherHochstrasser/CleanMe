package ch.bretscherhochstrasser.cleanme.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ch.bretscherhochstrasser.cleanme.MainActivity
import ch.bretscherhochstrasser.cleanme.R
import ch.bretscherhochstrasser.cleanme.annotation.AppContext
import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import timber.log.Timber
import toothpick.InjectConstructor

/**
 * Handles creation and updates to notifications
 */
@InjectConstructor
class NotificationHelper(
    @AppContext private val context: Context,
    private val notificationManager: NotificationManagerCompat
) {

    companion object {
        const val NOTIFICATION_ID_SERVICE = 123
        const val NOTIFICATION_ID_REMINDER = 234
        private const val CHANNEL_ID_SERVICE = "service_notification"
        private const val CHANNEL_ID_REMINDER = "reminder_notification"
    }

    fun createServiceNotification(deviceUsageStats: DeviceUsageStats): Notification {
        val title = context.getString(R.string.notification_service_title)
        val formattedUseTime = formatHoursAndMinutes(deviceUsageStats.deviceUseDuration)
        val text = context.getString(R.string.notification_service_text, formattedUseTime)
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID_SERVICE
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(ContextCompat.getColor(context, R.color.primaryColor))
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(mainActivityPendingIntent)
        return builder.build()
    }

    fun updateServiceNotification(deviceUsageStats: DeviceUsageStats) {
        Timber.d("Updating service notification")
        notificationManager.notify(
            NOTIFICATION_ID_SERVICE,
            createServiceNotification(deviceUsageStats)
        )
    }

    fun showReminderNotification() {
        Timber.d("Showing reminder notification")
        val title = context.getString(R.string.notification_reminder_title)
        val message = context.getString(R.string.notification_reminder_text)
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID_REMINDER
        )
            .setSmallIcon(R.drawable.ic_drop_24dp)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, R.color.primaryColor))
            .setLargeIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_drop_reminder)?.toBitmap()
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainActivityPendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID_REMINDER, builder.build())
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceNotificationChannel = NotificationChannel(
                CHANNEL_ID_SERVICE,
                context.getString(R.string.notification_channel_name_service),
                NotificationManager.IMPORTANCE_LOW
            )
            createNotificationChannel(serviceNotificationChannel)

            val reminderNotificationChannel = NotificationChannel(
                CHANNEL_ID_REMINDER,
                context.getString(R.string.notification_channel_name_reminder),
                NotificationManager.IMPORTANCE_HIGH
            )
            createNotificationChannel(reminderNotificationChannel)
        }
    }

    private val mainActivityPendingIntent: PendingIntent
        get() {
            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            return PendingIntent.getActivity(context, 0, mainActivityIntent, 0)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channel: NotificationChannel) {
        notificationManager.createNotificationChannel(channel)
        Timber.d("Notification channel '%s' created", channel.id)
    }

}
package ch.bretscherhochstrasser.cleanme.service

import ch.bretscherhochstrasser.cleanme.deviceusage.DeviceUsageStats
import ch.bretscherhochstrasser.cleanme.helper.NotificationHelper
import ch.bretscherhochstrasser.cleanme.settings.AppSettings
import timber.log.Timber
import toothpick.InjectConstructor
import javax.inject.Singleton

/**
 * Triggers the clean reminder.
 */
@Singleton
@InjectConstructor
class ReminderManager(
    private val appSettings: AppSettings,
    private val notificationHelper: NotificationHelper
) {

    private var reminderShown = false;

    /**
     * Checks whether the clean reminder should be shown and triggers the notification if necessary.
     * This function can be safely called every time [DeviceUsageStats] are updated.
     */
    fun showReminderIfRequired(deviceUsageStats: DeviceUsageStats) {
        if (deviceUsageStats.deviceUseDuration > appSettings.cleanInterval.durationMillis) {
            Timber.d("Device use time > clean interval: clean reminder needed")
            if (reminderShown) {
                Timber.d("Already showing reminder. Not triggering again.")
            } else {
                Timber.d("Not showing reminder. Triggering notification.")
                notificationHelper.showReminderNotification()
                reminderShown = true
            }
        }
    }

    /**
     * Resets the reminder shown flag. This should be invoked on reset / clean, so a new reminder
     * can be shown in the future.
     */
    fun reset() {
        reminderShown = false
    }
}